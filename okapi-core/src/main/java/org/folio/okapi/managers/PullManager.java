package org.folio.okapi.managers;

import io.vertx.core.json.Json;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.logging.Logger;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.folio.okapi.bean.ModuleDescriptor;
import org.folio.okapi.bean.PullDescriptor;
import org.folio.okapi.common.ErrorType;
import org.folio.okapi.common.ExtendedAsyncResult;
import org.folio.okapi.common.Failure;
import org.folio.okapi.common.Messages;
import org.folio.okapi.common.OkapiLogger;
import org.folio.okapi.common.Success;

@java.lang.SuppressWarnings({"squid:S1192"})
public class PullManager {

  private final Logger logger = OkapiLogger.get();
  private final HttpClient httpClient;
  private int concurrentRuns;
  private static final int CONCURRENT_MAX = 10;
  private static final int FULL_FETCH_MIN = 100;
  private boolean concurrentComplete;
  private final ModuleManager moduleManager;
  private Messages messages = Messages.getInstance();

  public PullManager(Vertx vertx, ModuleManager moduleManager) {
    this.httpClient = vertx.createHttpClient();
    this.moduleManager = moduleManager;
  }

  private void getRemoteUrl(Iterator<String> it,
    Handler<ExtendedAsyncResult<String>> fut) {
    if (!it.hasNext()) {
      fut.handle(new Failure<>(ErrorType.NOT_FOUND, messages.getMessage("11000")));
    } else {
      final String baseUrl = it.next();
      String url = baseUrl;
      if (!url.endsWith("/")) {
        url += "/";
      }
      url += "_/version";
      final Buffer body = Buffer.buffer();
      HttpClientRequest req = httpClient.getAbs(url, res -> {
        res.handler(body::appendBuffer);
        res.endHandler(x -> {
          if (res.statusCode() != 200) {
            logger.info("pull for " + baseUrl + " failed with status "
                    + res.statusCode());
            fut.handle(new Failure<>(ErrorType.USER,
              "pull for " + baseUrl + " returned status " + res.statusCode() + "\n" + body.toString()));
          } else {
            fut.handle(new Success<>(baseUrl));
          }
        });
        res.exceptionHandler(x
          -> fut.handle(new Failure<>(ErrorType.INTERNAL, x.getMessage()))
        );
      });
      req.exceptionHandler(res -> {
        logger.info("pull for " + baseUrl + " failed with status "
                + res.getMessage());
        getRemoteUrl(it, fut);
      });
      req.end();
    }
  }

  private void getList(String urlBase, boolean full,
    Handler<ExtendedAsyncResult<ModuleDescriptor[]>> fut) {
    StringBuilder url = new StringBuilder(urlBase);
    if (!urlBase.endsWith("/")) {
      url.append("/");
    }
    url.append("_/proxy/modules");
    if (full) {
      url.append("?full=true");
    }
    final Buffer body = Buffer.buffer();
    logger.info("GET " + url);
    HttpClientRequest req = httpClient.getAbs(url.toString(), res -> {
      logger.info("GET " + url + " response " + res.statusCode());
      res.handler(body::appendBuffer);
      res.endHandler(x -> {
        if (res.statusCode() != 200) {
          fut.handle(new Failure<>(ErrorType.USER, body.toString()));
        } else {
          ModuleDescriptor[] ml = Json.decodeValue(body.toString(),
            ModuleDescriptor[].class);
          fut.handle(new Success<>(ml));
        }
      });
      res.exceptionHandler(x
        -> fut.handle(new Failure<>(ErrorType.INTERNAL, x.getMessage())));
    });
    req.putHeader("Accept", "application/json");
    req.exceptionHandler(x
      -> fut.handle(new Failure<>(ErrorType.INTERNAL, x.getMessage())));
    req.end();
  }

  private void getFull(String urlBase, Iterator<ModuleDescriptor> it,
    List<ModuleDescriptor> ml,
    Handler<ExtendedAsyncResult<List<ModuleDescriptor>>> fut) {

    while (!concurrentComplete && concurrentRuns < CONCURRENT_MAX && it.hasNext()) {
      ++concurrentRuns;
      String url = urlBase;
      if (!url.endsWith("/")) {
        url += "/";
      }
      url += "_/proxy/modules/" + it.next().getId();
      getFullReq(url, fut, ml, urlBase, it);
    }
    if (!it.hasNext() && !concurrentComplete && concurrentRuns == 0) {
      concurrentComplete = true;
      fut.handle(new Success<>(ml));
    }
  }

  private void getFullReq(String url, Handler<ExtendedAsyncResult<List<ModuleDescriptor>>> fut,
    List<ModuleDescriptor> ml, String urlBase, Iterator<ModuleDescriptor> it) {

    final Buffer body = Buffer.buffer();
    HttpClientRequest req = httpClient.getAbs(url, res -> {
      res.handler(body::appendBuffer);
      res.endHandler(x -> {
        if (concurrentRuns > 0) {
          concurrentRuns--;
        }
        if (res.statusCode() != 200) {
          if (!concurrentComplete) {
            concurrentComplete = true;
            fut.handle(new Failure<>(ErrorType.USER, body.toString()));
          }
        } else {
          ModuleDescriptor md = Json.decodeValue(body.toString(),
            ModuleDescriptor.class);
          ml.add(md);
          getFull(urlBase, it, ml, fut);
        }
      });
      res.exceptionHandler(x -> {
        if (concurrentRuns > 0) {
          concurrentRuns--;
        }
        if (!concurrentComplete) {
          concurrentComplete = true;
          fut.handle(new Failure<>(ErrorType.INTERNAL, x.getMessage()));
        }
      });
    });
    req.exceptionHandler(x -> {
      if (concurrentRuns > 0) {
        concurrentRuns--;
      }
      if (!concurrentComplete) {
        concurrentComplete = true;
        fut.handle(new Failure<>(ErrorType.INTERNAL, x.getMessage()));
      }
    });
    req.end();
  }

  private void localInsert(List<ModuleDescriptor> mlList, List<ModuleDescriptor> mlAdd,
    Handler<ExtendedAsyncResult<List<ModuleDescriptor>>> fut) {
    logger.info("pull: local insert");

    moduleManager.createList(mlList, true, true, true, res1 -> {
      if (res1.failed()) {
        fut.handle(new Failure<>(res1.getType(), res1.cause()));
      } else {
        fut.handle(new Success<>(mlAdd));
      }
    });

  }

  private void merge(String urlBase, boolean incremental, List<ModuleDescriptor> mlLocal,
    ModuleDescriptor[] mlRemote, Handler<ExtendedAsyncResult<List<ModuleDescriptor>>> fut) {

    Set<String> enabled = new TreeSet<>();
    for (ModuleDescriptor md : mlLocal) {
      enabled.add(md.getId());
    }
    List<ModuleDescriptor> mlAdd = new LinkedList<>();
    for (ModuleDescriptor md : mlRemote) {
      if (!"okapi".equals(md.getProduct()) && !enabled.contains(md.getId())) {
        mlAdd.add(md);
      }
    }
    final List<ModuleDescriptor> mlList = new LinkedList<>();
    // disable incremental mode if more than a certain
    // number of MDs are to be fetched
    if (incremental && mlAdd.size() >= FULL_FETCH_MIN) {
      incremental = false;
    }
    logger.info("pull: " + mlAdd.size() + " MDs to fetch incremental=" + incremental);
    if (!incremental) {
      getList(urlBase, true, res -> {
        if (res.failed()) {
          fut.handle(new Failure<>(res.getType(), res.cause()));
          return;
        }
        ModuleDescriptor[] rList = res.result();
        for (int i = 0; i < rList.length; i++) {
          ModuleDescriptor rMd = rList[i];
          if (!"okapi".equals(rMd.getProduct()) && !enabled.contains(rMd.getId())) {
            mlList.add(rMd);
          }
        }
        localInsert(mlList, mlAdd, fut);
      });
    } else {
      concurrentRuns = 0;
      concurrentComplete = false;
      getFull(urlBase, mlAdd.iterator(), mlList, res -> {
        if (res.failed()) {
          fut.handle(new Failure<>(res.getType(), res.cause()));
        } else {
          localInsert(mlList, mlAdd, fut);
        }
      });
    }
  }

  public void pull(PullDescriptor pd, Boolean incremental,
    Handler<ExtendedAsyncResult<List<ModuleDescriptor>>> fut) {

    getRemoteUrl(Arrays.asList(pd.getUrls()).iterator(), resUrl -> {
      if (resUrl.failed()) {
        fut.handle(new Failure<>(resUrl.getType(), resUrl.cause()));
      } else {
        moduleManager.getModulesWithFilter(true, true, resLocal -> {
          if (resLocal.failed()) {
            fut.handle(new Failure<>(resLocal.getType(), resLocal.cause()));
          } else {
            getList(resUrl.result(), false, resRemote -> {
              if (resRemote.failed()) {
                fut.handle(new Failure<>(resRemote.getType(), resRemote.cause()));
              } else {
                merge(resUrl.result(), incremental, resLocal.result(), resRemote.result(), fut);
              }
            });
          }
        });
      }
    });
  }
}

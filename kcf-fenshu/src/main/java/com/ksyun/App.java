package com.ksyun;

import io.javalin.Javalin;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.http.HttpMessageFactory;
import io.cloudevents.jackson.PojoCloudEventDataMapper;

import static io.cloudevents.core.CloudEventUtils.mapData;

import lombok.extern.slf4j.Slf4j;

import com.ksyun.KS3EventDataModel.Ks3CloudEventData;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


@Slf4j
public class App {
    public static void main(String[] args) {
        System.out.println("Hello KCF!");
        Javalin app = Javalin.create(
                config -> {
                    config.requestLogger((ctx, ms) -> {
                        log.info(ctx.body());
                    });
                }).start(8080);
        app.get("/", ctx -> ctx.result("Hello KingSoft Function"));

        // Health check entry.
        app.get("/health", ctx -> {
            ctx.result("Up");
        });

        // KCF function entry.
        app.post("/event-invoke", ctx -> {
            log.info("starting process...");
            CloudEvent ce = HttpMessageFactory.createReader(ctx.headerMap(), ctx.body().getBytes()).toEvent();
            ObjectMapper objMapper = new ObjectMapper();
            PojoCloudEventData<Ks3CloudEventData> cloudEventData = mapData(ce,
                    PojoCloudEventDataMapper.from(objMapper, Ks3CloudEventData.class));
            Ks3CloudEventData ks3EventData = cloudEventData.getValue();
            doProcess(ks3EventData);
            log.info("done processing...");
            ctx.result("ok");

        });
    }

    private static void doProcess(Ks3CloudEventData ks3EventData) {
        // Show how to get necessary data from KS3 event data.
        // Add your own process in this function. 
        if (ks3EventData != null) {
            log.info("KCF.DEMO: {}", ks3EventData.toString());
            log.info("KCF.DEMO: {}", ks3EventData.getKs3().toString());
            log.info("KCF.DEMO: {}", ks3EventData.getKs3().getObject().toString());
            String msg = "上传文件[%s]到[%s]桶中";
            SendMessageToFeiShu(String.format(msg, ks3EventData.getKs3().getObject().getKey(), ks3EventData.getKs3().getBucket().getName()));
        } else {
            log.error("KCF.DEMO: {}", "Event data disrupted! ");
        }
    }

    public static void SendMessageToFeiShu(String msg) {
        // Get forward proxy IP address and port from ENV.
        final String forwardProxyIp = System.getenv("PROXY_IP");
        final int forwardProxyPort = Integer.parseInt(System.getenv("PROXY_PORT"));
        // Get url of FeiShu Bot ID from ENV
        final String WebhookBotId = System.getenv("WEBHOOK_ID");
        // PLEASE Confirm 'forwardProxyIp' with CORRESPOND IP ADDRESS of PROXY Server which in the same VPN with KCF instance!!!
        final Proxy vpcProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(forwardProxyIp, forwardProxyPort));
        final MediaType JSON = MediaType.get("application/json; charset=utf-8");
        final OkHttpClient client = new OkHttpClient().newBuilder().proxy(vpcProxy).build();
        // FeiShu notifyURL
        String notifyURL = String.format("https://open.feishu.cn/open-apis/bot/v2/hook/%s", WebhookBotId);
        String postBody = "{\"msg_type\":\"text\",\"content\":{\"text\":\"%s\"}}";
        RequestBody body = RequestBody.create(String.format(postBody, msg), JSON);
        Request request = new Request.Builder()
                .url(notifyURL)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            log.info("KCF.DEMO: {}", "Sent message to FeiShu success.");
        } catch (IOException ex) {
            log.error("KCF.DEMO: {}", "Error while send message to FeiShu!");
            log.error("KCF.DEMO: {}", ex.toString());
        }
    }
}

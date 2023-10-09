package com.minscript.news;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minscript.config.repository.NewsRepository;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

@Service
@Slf4j
public class NewsService {

    @Autowired
    private final NewsRepository repository;
    private final WebClient webClient;

    @Autowired
    public NewsService(@Value("${open-api.client-id}") String clientId, @Value("${open-api.client-secret}") String clientSecret,NewsRepository newsRepository) {

       this.repository=newsRepository;

        TcpClient tcpClient = TcpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .doOnConnected(conn -> {
                    conn.addHandlerLast(new ReadTimeoutHandler(5000));
                    conn.addHandlerLast(new WriteTimeoutHandler(5000));
                });

        this.webClient = WebClient.builder()
                .baseUrl("https://openapi.naver.com")
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-Naver-Client-Id", clientId) // 네이버 클라이언트 아이디
                .defaultHeader("X-Naver-Client-Secret", clientSecret) // 네이버 클라이언트 시크릿
                .build();
    }

    public JsonNode findTodayNews(String key){
        String json = webClient.get()
                .uri("/v1/search/news.json?query={key}&display=10",key)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper om = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try{

            JsonNode jsonNode = om.readTree(json);
            log.info(String.valueOf(jsonNode));

            return jsonNode;


        }catch (Exception e){
            e.printStackTrace();
        }

return null;
    }
}

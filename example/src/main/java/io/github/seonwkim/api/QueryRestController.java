package io.github.seonwkim.api;

import static io.github.seonwkim.api.ApiPath.DELETE_V1;
import static io.github.seonwkim.api.ApiPath.INSERT_V1;
import static io.github.seonwkim.api.ApiPath.SELECT_V1;
import static io.github.seonwkim.api.ApiPath.UPDATE_V1;

import org.springframework.stereotype.Component;

import io.github.seonwkim.service.QueryService;

import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.server.annotation.Blocking;
import com.linecorp.armeria.server.annotation.Delete;
import com.linecorp.armeria.server.annotation.Post;
import com.linecorp.armeria.server.annotation.Put;

@Blocking
@Component
public class QueryRestController {

    private final QueryService queryService;

    public QueryRestController(QueryService queryService) {
        this.queryService = queryService;
    }

    @Post(SELECT_V1)
    public HttpResponse select(QueryRequest request) {
        return HttpResponse.ofJson(HttpStatus.OK, queryService.select(request.query()));
    }

    @Post(INSERT_V1)
    public HttpResponse insert(QueryRequest request) {
        return HttpResponse.ofJson(HttpStatus.OK, queryService.insert(request.query()));
    }

    @Put(UPDATE_V1)
    public HttpResponse update(QueryRequest request) {
        return HttpResponse.ofJson(HttpStatus.OK, queryService.update(request.query()));
    }

    @Delete(DELETE_V1)
    public HttpResponse delete(QueryRequest request) {
        return HttpResponse.ofJson(HttpStatus.OK, queryService.delete(request.query()));
    }
}

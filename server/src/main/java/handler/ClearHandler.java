package handler;

import com.google.gson.Gson;
import requests.ClearRequest;
import responses.ClearResponse;
import responses.ErrorResponseClass;
import service.ClearDbService;
import spark.Request;
import spark.Response;

public class ClearHandler {
    private final ClearDbService clearService;

    public ClearHandler(ClearDbService clearService){
        this.clearService = clearService;
    }

    public Object handleClear(Request clearReq, Response clearResp) {
        Gson gson = new Gson();

        try{
            ClearRequest clearRequest = new ClearRequest();
            ClearResponse clearResponse = clearService.clear(clearRequest);

            clearResp.status(200);
            return gson.toJson(clearResponse);

        } catch (Exception e){
            clearResp.status(500);
            return gson.toJson(new ErrorResponseClass(e.getMessage()));
        }
    }
}

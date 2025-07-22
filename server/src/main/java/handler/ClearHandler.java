package handler;

import com.google.gson.Gson;
import dataaccess.exceptions.DatabaseAccessException;
import requests.ClearRequest;
import responses.ClearResponse;
import responses.ErrorResponseClass;
import service.ClearDbService;
import spark.Request;
import spark.Response;

public class ClearHandler {
    private final ClearDbService clearService;

    public ClearHandler(ClearDbService clearService) {
        this.clearService = clearService;
    }

    /**
     * Handler Method for the clear endpoint. Takes in Json Requests and Responses and parses them before sending them
     * to the ClearDbService.
     *
     * @param clearReq  the HTTP request sent by the client
     * @param clearResp the HTTP response object, used to set the response code
     * @return a Json string representing a clearResponse object on success, or an ErrorResponseClass object on failure
     */
    public Object handleClear(Request clearReq, Response clearResp) {
        Gson gson = new Gson();

        try {
            ClearRequest clearRequest = new ClearRequest();
            ClearResponse clearResponse = clearService.clear(clearRequest);

            clearResp.status(200);
            return gson.toJson(clearResponse);

        } catch (DatabaseAccessException e) {
            clearResp.status(500);
            return gson.toJson(new ErrorResponseClass(e.getMessage()));
        }
    }
}

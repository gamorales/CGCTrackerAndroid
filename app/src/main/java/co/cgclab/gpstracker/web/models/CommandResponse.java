package co.cgclab.gpstracker.web.models;

public class CommandResponse {
    int success;
    String data;

    public CommandResponse() {}

    public CommandResponse(int success, String data) {
        this.success = success;
        this.data = data;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

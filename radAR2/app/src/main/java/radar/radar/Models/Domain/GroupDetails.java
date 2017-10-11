package radar.radar.Models.Domain;

import java.util.List;

/**
 * Created by kenneth on 6/9/17.
 */


public class GroupDetails {

    private Boolean success;

    private List<Object> errors = null;

    private Info info;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<Object> getErrors() {
        return errors;
    }

    public void setErrors(List<Object> errors) {
        this.errors = errors;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }
}

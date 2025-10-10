package api.skeleton.interfaces;

import api.Models.BaseModel;

public interface CrudEndpointInterface {

    Object post(BaseModel baseModel);

    Object get();

    Object update(BaseModel baseModel);

    Object delete(long id);
}

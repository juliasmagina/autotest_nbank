package Requests.skeleton.interfaces;

import Models.BaseModel;

public interface CrudEndpointInterface {

    Object post(BaseModel baseModel);

    Object get();

    Object update(BaseModel baseModel);

    Object delete(long id);
}

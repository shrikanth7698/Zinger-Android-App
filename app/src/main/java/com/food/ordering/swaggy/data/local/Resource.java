package com.food.ordering.swaggy.data.local;

public class Resource<T> {

    public static final int SUCCESS = 0;
    public static final int ERROR = 1;
    public static final int NO_INTERNET = 2;
    public static final int EMPTY = 3;
    public static final int LOADING = 4;

    public int status;
    public T data = null;
    public String message = "";

    public Resource loading() {
        Resource resource = new Resource();
        resource.status = LOADING;
        resource.message = "Loading";
        return resource;
    }

    public Resource success(T value) {
        Resource resource = new Resource();
        resource.status = SUCCESS;
        resource.message = "Success";
        resource.data = value;
        return resource;
    }

    public Resource error(String message) {
        Resource resource = new Resource();
        resource.status = ERROR;
        resource.message = message;
        return resource;
    }

    public Resource offline() {
        Resource resource = new Resource();
        resource.status = NO_INTERNET;
        resource.message = "No Internet Connection";
        return resource;
    }

    public Resource empty() {
        Resource resource = new Resource();
        resource.status = EMPTY;
        return resource;
    }
}

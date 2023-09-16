package org.example.common;

public class Response {

    private Object data;

    private Long id;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Response{" +
                "data=" + data +
                ", id=" + id +
                '}';
    }
}

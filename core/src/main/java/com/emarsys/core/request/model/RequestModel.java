package com.emarsys.core.request.model;

import com.emarsys.core.provider.uuid.UUIDProvider;
import com.emarsys.core.provider.timestamp.TimestampProvider;
import com.emarsys.core.util.Assert;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RequestModel implements Serializable {
    private URL url;
    private RequestMethod method;
    private Map<String, Object> payload;
    private Map<String, String> headers;
    private long timestamp;
    private long ttl;
    private String id;

    public RequestModel(String url, RequestMethod method, Map<String, Object> payload, Map<String, String> headers, long timestamp, long ttl, String id) {
        Assert.notNull(url, "Url must not be null!");
        Assert.notNull(method, "Method must not be null!");
        Assert.notNull(headers, "Headers must not be null!");
        Assert.notNull(id, "Id must not be null!");
        try {
            this.url = new URL(url);
        } catch (MalformedURLException mue) {
            throw new IllegalArgumentException(mue);
        }
        this.method = method;
        this.payload = payload;
        this.headers = headers;
        this.timestamp = timestamp;
        this.ttl = ttl;
        this.id = id;
    }

    public URL getUrl() {
        return url;
    }

    public RequestMethod getMethod() {
        return method;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getTtl() {
        return ttl;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RequestModel that = (RequestModel) o;

        if (timestamp != that.timestamp) return false;
        if (ttl != that.ttl) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (method != that.method) return false;
        if (payload != null ? !payload.equals(that.payload) : that.payload != null) return false;
        if (headers != null ? !headers.equals(that.headers) : that.headers != null) return false;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (method != null ? method.hashCode() : 0);
        result = 31 * result + (payload != null ? payload.hashCode() : 0);
        result = 31 * result + (headers != null ? headers.hashCode() : 0);
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (int) (ttl ^ (ttl >>> 32));
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RequestModel{" +
                "url=" + url +
                ", method=" + method +
                ", payload=" + payload +
                ", headers=" + headers +
                ", timestamp=" + timestamp +
                ", ttl=" + ttl +
                ", id='" + id + '\'' +
                '}';
    }

    public static class Builder {
        private String url;
        private RequestMethod method;
        private Map<String, Object> payload;
        private Map<String, String> headers;
        private long timestamp;
        private long ttl;
        private String id;

        public Builder(TimestampProvider timestampProvider, UUIDProvider uuidProvider) {
            method = RequestMethod.POST;
            headers = new HashMap<>();
            timestamp = timestampProvider.provideTimestamp();
            ttl = Long.MAX_VALUE;
            id = uuidProvider.provideId();
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder method(RequestMethod method) {
            this.method = method;
            return this;
        }

        public Builder payload(Map<String, Object> payload) {
            this.payload = payload;
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder ttl(long ttl) {
            this.ttl = ttl;
            return this;
        }

        public RequestModel build() {
            return new RequestModel(url, method, payload, headers, timestamp, ttl, id);
        }
    }
}
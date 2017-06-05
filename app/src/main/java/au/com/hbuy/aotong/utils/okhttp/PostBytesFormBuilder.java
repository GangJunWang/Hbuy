package au.com.hbuy.aotong.utils.okhttp;

import com.zhy.http.okhttp.builder.HasParamsable;
import com.zhy.http.okhttp.builder.OkHttpRequestBuilder;
import com.zhy.http.okhttp.request.RequestCall;
import com.zhy.http.okhttp.utils.Exceptions;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @time: 2016/2/27 11:04
 */
public class PostBytesFormBuilder extends OkHttpRequestBuilder<PostBytesFormBuilder> implements HasParamsable {
    private FileInput fileInput;

    @Override
    public RequestCall build() {
        return new PostBytesFormRequest(url, tag, params, headers, fileInput, id).build();
    }

    public PostBytesFormBuilder addByteArray(String name, String filename, final byte[] content) {
        if (name == null) {
            Exceptions.illegalArgument("the key can not be null !");
        }
        if (filename == null) {
            Exceptions.illegalArgument("the name of file can not be null !");
        }
        fileInput = new FileInput(name, filename, content);
        return this;
    }

    public static class FileInput {
        public String key;
        public String filename;
        public byte[] content;

        public FileInput(String name, String filename, byte[] content) {
            this.key = name;
            this.filename = filename;
            this.content = content;
        }

        @Override
        public String toString() {
            return "FileInput{" +
                    "key='" + key + '\'' +
                    ", filename='" + filename + '\'' +
                    ", content=" + content +
                    '}';
        }
    }


    @Override
    public PostBytesFormBuilder params(Map<String, String> params) {
        this.params = params;
        return this;
    }

    @Override
    public PostBytesFormBuilder addParams(String key, String val) {
        if (this.params == null) {
            params = new LinkedHashMap<>();
        }
        params.put(key, val);
        return this;
    }

}

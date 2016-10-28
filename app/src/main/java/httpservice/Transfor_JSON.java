package httpservice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/7/5 0005.
 */
public class Transfor_JSON {

    JSONArray jsonArray;

    JSONObject jsonObject;

    public JSONObject toJSON(String[] string){

        if (string != null){

            jsonObject = new JSONObject();
            try {
                for(int i=0;i<(string.length-1);i=i+2)
                jsonObject.put(string[i],string[i+1]);
                return jsonObject;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

package net.xzos.UpgradeAll;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UpgradeItemSettingActivity extends AppCompatActivity {

    private static final String TAG = "UpgradeItemSettingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JSONObject versionChecker = new JSONObject();
        setContentView(R.layout.activity_upgrade_item_setting);
        Button versionCheckButton = findViewById(R.id.versionCheckButton);
        versionCheckButton.setOnClickListener(v -> {
            // 版本检查设置
            Spinner versionCheckSpinner = findViewById(R.id.versionCheckSpinner);
            EditText editVersionChecker = findViewById(R.id.editVersionChecker);
            String versionCheckerText = editVersionChecker.getText().toString();
            String versionCheckerApi = versionCheckSpinner.getSelectedItem().toString();
            switch (versionCheckerApi) {
                case "APP 版本":
                    versionCheckerApi = "APP";
                    break;
            }
            try {
                versionChecker.put("api", versionCheckerApi);
                versionChecker.put("text", versionCheckerText);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            switch (versionCheckerApi) {
                case "APP":
                    String appVersion = VersionChecker.getAppVersion(versionChecker);
                    if (appVersion != null) {
                        Toast.makeText(UpgradeItemSettingActivity.this, appVersion, Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        });
        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> {
            EditText editName = findViewById(R.id.editName);
            EditText editUrl = findViewById(R.id.editUrl);
            String name = editName.getText().toString();
            String url = editUrl.getText().toString();
            Spinner apiSpinner = findViewById(R.id.api_spinner);
            String api = apiSpinner.getSelectedItem().toString();
            String versionCheckerText = null;
            try {
                versionCheckerText = String.valueOf(versionChecker.get("text"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (versionCheckerText != null && versionCheckerText.length() != 0) {
                boolean addRepoSuccess = addRepoDatabase(name, api, url, versionChecker);
                if (addRepoSuccess) {
                    Intent intent = new Intent(UpgradeItemSettingActivity.this, MainActivity.class);
                    startActivity(intent);
                    // 跳转主页面
                } else {
                    Toast.makeText(UpgradeItemSettingActivity.this, "什么？数据库添加失败！", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    static boolean addRepoDatabase(String name, String api, String url, JSONObject versionChecker) {
        // TODO: 可被忽略的参数
        if (api.length() != 0 && url.length() != 0) {
            String owner = "";
            String repo = "";
            String api_url = "";
            switch (api.toLowerCase()) {
                case "github":
                    String[] temp = url.split("github\\.com");
                    temp = temp[temp.length - 1].split("/");
                    List<String> list = new ArrayList<>(Arrays.asList(temp));
                    list.removeAll(Arrays.asList("", null));
                    owner = list.get(0);
                    repo = list.get(1);
                    // 分割网址
                    api_url = "https://api.github.com/repos/"
                            + owner + "/" + repo + "/releases";
                    break;
            }
            if (name.length() == 0) {
                name = repo;
                // 如果未自定义名称，则使用仓库名
            }
            RepoDatabase repoDatabase = new RepoDatabase();
            // 开启数据库
            LitePal.deleteAll(RepoDatabase.class, "api_url = ?", api_url);
            // 删除所有数据库重复项
            repoDatabase.setName(name);
            repoDatabase.setApi(api);
            repoDatabase.setUrl(url);
            repoDatabase.setApiUrl(api_url);
            Log.d(TAG, "addRepoDatabase:  version" + versionChecker);
            repoDatabase.setVersionChecker(versionChecker);
            repoDatabase.save();
            // 将数据存入 RepoDatabase 数据库
            return true;
        }
        return false;
    }
}
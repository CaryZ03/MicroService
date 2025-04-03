package com.dofinal.RG.login;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * &#064;Classname handleLogin
 * &#064;TODO for 张瀚文：
 * 这个类用于处理用户的 登录或注册信息（两个都有!).你应该设计若干方法，使得调用execute()方法时，
 * 这个类会针对用户输入的LoginMessage做出一系列的处理。具体处理方式请参见群里的TODO.pdf文档。
 * 如果成功，你应该实例化一个User对象。关于User，请看users包中的描述。
 * 注意：数据库的查询和更新功能在tools板块中寻找。
 * TIPS：可以想想我们登录或注册的具体流程。
 * &#064;Date 2024/4/22 20:21
 * &#064;Created MuJue
 */
public class handleLogin{
    LoginMessage loginMessage;
    String uid;
    String password;
    String type;

    public handleLogin(String uid, String password,String type){
        loginMessage = new LoginMessage(uid, password, type);
        this.uid = uid;
        this.password = password;
        this.type = type;
    }

    private boolean Login(){
//        QueryTool queryTool = QueryTool.getInstance();
//        HashMap<String, String> queryMap = new HashMap<>();
//        HashMap<String, String> rightMessage = new HashMap<>();
//        queryMap.put("table_name", uid);
//        rightMessage = queryTool.startQuery(queryMap);
//        if(this.password.equals(rightMessage.get("password"))){
//            User user = new User(rightMessage.get("uid"),rightMessage.get("password"), rightMessage.get("telNumber"), rightMessage.get("gender"), rightMessage.get("pid"), rightMessage.get("location"));
//            return true;
//        }
        return false;
    }

    private boolean Register(){
        if(!IllegalUid()){
            return false;
        }
        if(!CheckIfExist()){
            return false;
        }
        return IllegalPassword();
//        UpdateTool updateTool = UpdateTool.getInstance();
//        HashMap<String, String> registerMap = new HashMap<>();
//        registerMap.put("uid", uid);
//        registerMap.put("password", password);
//        updateTool.startUpdate(registerMap);//先上传到数据库
//
//        QueryTool queryTool = QueryTool.getInstance();
//        HashMap<String, String> queryMap = new HashMap<>();
//        HashMap<String, String> rightMessage = new HashMap<>();
//        queryTool.startQuery(registerMap);//从数据库进行读取
//        queryMap.put("table_name", uid);
//        rightMessage = queryTool.startQuery(queryMap);
//        User user = new User(rightMessage.get("uid"), rightMessage.get("password"), rightMessage.get("telNumber"), rightMessage.get("gender"), rightMessage.get("pid"), rightMessage.get("location"));
    }

    private boolean IllegalUid(){
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,16}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private boolean CheckIfExist(){
//        QueryTool queryTool = QueryTool.getInstance();
//        HashMap<String, String> queryMap = new HashMap<>();
//        queryMap.put("table_name", uid);
//        return queryTool.startQuery(queryMap) == null;
        return false;
    }

    private boolean IllegalPassword(){
        if (password.length() < 8 || password.length() > 16) {
            return false;
        }
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,16}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}

# WiFiPassword
本应用的原理很简单，通过读取系统目录 "/data/misc/wifi/*.conf" 中保存的WiFi密码，来实现功能；
本应用除了获取root权限以及sd卡读写权限之外没有获取任何其他权限；
本应用参照了 cfm1120 的开源项目 seepwd（https://github.com/cfm1120/seepwd），wang4yu6peng13的
        开源项目 SeeWifiPwd（https://github.com/wang4yu6peng13/SeeWifiPwd） ，同时本项目也开源，
        地址为：https://github.com/juicecwc/WiFiPassword

Tips：
    ◎手动删除的 Wifi 无法获取。
    ◎单击列表里的 Wifi 名称和密码可以复制。
    ◎有密码的WiFi排在前面。
    ◎可以设置显示无密码WiFi或者不显示。
    ◎可以设置WiFi列表的排序方式。

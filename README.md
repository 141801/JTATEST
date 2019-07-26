For code explanation and additional configurations read the tutorial at https://javatutorial.net/java-servlet-example
# 1.データベース環境作成 <p>
    ※環境構築はRHEL７である
    1)dockerのインストール
         $sudo yum install yum-utils
         $sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
         $sudo yum repolist -v
         $yum list docker-ce --showduplicates | sort -r
         $sudo yum install docker-ce
         $sudo systemctl enable --now docker 
         $systemctl is-active docker  #active確認
    2)docker-composeのインストール
         $curl -L "https://github.com/docker/compose/releases/download/1.23.2/docker-compose-$(uname -s)-$(uname -m)" -o docker-compose
         $sudo mv docker-compose /usr/local/bin && sudo chmod +x /usr/local/bin/docker-compose
    3)Mysql用docker VM作成
         $cd docker_mysql
         $docker-compose up &
    4)Mysql コンテナ のIPアドレス確認
          $docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}'　コンテナID   #自分の場合には172.18.0.2
# 2 glassfishにおけるJDBC環境作成 <p>

1.  MySQL :: Download Connector/J zip(oracle offical site)ファイルに同梱されているmysql-connector-java-XXX.jarをdomains/domain1/lib/extにコピー
2.  bin/asadmin start-domainでglassfishサーバ起動する
3.  http://IPアドレス:4848で管理画面に入って、左メニューからJDBC→JDBC Connection Poolを選択する。「New...」ボタンを押下し、接続情報入力
    

* Pool Name: 任意(今回はmypoolにしておく)  
* Resource Type: javax.sql.DataSource
* Database Driver Vendor: MySQL  <p>
![basic](https://i.ibb.co/7Kf1sM8/image.png "")　<p>

4.  Additional Propertiesには以下の情報を入力

 ※ddocker-compose.yml と同じよにpasswordとuserrを設定する<p>
* password:MYSQL
* user: MYSQL
* URL: jdbc:mysql://172.18.0.2/MYSQL?allowPublicKeyRetrieval=true&useSSL=false <p>

![additional](https://i.ibb.co/4Fg9pKW/image.png "")　<p>

5. pingでデータベースにアクセス確認  <p>
![ping](https://i.ibb.co/hZsrbv1/image.png "")　<p>

6. JDBC Resources新規作成

下図のようなjdbc/mypoolを作成する<p>
![JDBC Resources](https://i.ibb.co/tpyy8B7/image.png "")　<p>


# 3 検証用war作成、配備<p>
         $mvn clean install
target配下にJTATest-1.warが作られたことを確認<p>
glassfishの作業ディレクトリに移動する<p>

         $bin/asadmin deploy 格納先/JTATest-1.war

# 4 結果確認<p>

glassfishサーバログに以下の出力を確認する
<pre>
Starting top-level transaction.]]
Adding entries to table 1.]]
Inspecting table 1.]]
 Column 1: 1]]
 Column 2: 2]]
Adding entries to table 2.]]
Inspecting table 2.]]
 Column 1: 3]]
 Column 2: 4]]
 
Now attempting to rollback changes.]]
Now checking state of table 1.]]
Now checking state of table 2.]]

JTATest-1 was successfully deployed in 339 milliseconds.]]
</pre>

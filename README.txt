■概要
このプロジェクトは、Jiemamyの各種ドキュメントを生成するプロジェクトです。

pom.xmlが配置されていますが、完全なMaven化には至っておらず、大部分はAntによるビルドになります。
ドキュメントのビルドは Ant を経由した、Velocity DocBook-Framework-1.0 をベースにしたシステムで
行っています。（日本語化対応等のため、フレームワークにも手が入っています）


■チェックアウト後の準備
このドキュメント構築システムはJava Advanced Imaging (JAI)に依存していますが、
このライブラリは再配布が許可されていない為、各自で配置する必要があります。（svn:ignore済みです）

http://download.java.net/media/jai/builds/release/1_1_3/jai-1_1_3-lib.zip

上記URLからJAIをダウンロードし、jai_core.jar と jai_codec.jar を src/DocBook-Framework-1.0/lib
ディレクトリにコピーしてください。

■antの設定確認
antを利用してビルドをおこないますので環境変数ANT_HOMEの設定、PATHの設定を確認してください。
コマンドライン上でant -versionを実行し、正しくVersionが返却されていることを確認してください。

■ドキュメントの生成
src/docbook/(editor-quickstart|editor-reference|dev-process|api-quickstart)内にdocbook形式のソースを配置してあります。
この状態でAntを引数なしで起動することにより target/(editor-quickstart|editor-reference|dev-process|api-quickstart) 内に
各種フォーマットのドキュメントが生成されます。

Eclipse上からAntを実行するとビルドに失敗することがありますので、コマンドライン上から実行することを推奨します。
ドキュメントプロジェクトのカレントディレクトリに移動した後、antコマンドを実行してください。
またWindowsVistaの場合、TrueFont関連でWarningやErrorが発生しますが問題ありませんので無視してください。

■ネットワークに関する注意
Ant実行時にインターネット経由でdtdの取得を行います。そのさい、インターネットに接続されていないとdocbook.xmlが見つからないとエラーが発生
します。また、プロキシ環境下にあるマシンでビルドを行う場合も同じようなエラーが発生します。
プロキシ環境下でのビルドに関しては現在調査中です。（propertyで設定できるかも？）

■プロジェクト構成
簡略化したプロジェクトディレクトリ構成は以下のとおり。
下記以外のディレクトリは、フレームワーク本体や、あまり編集する必要のない設定ファイル、またはビルドに
おける中間ファイルが占めています。あまり気にする必要がないところです。

ProjectRoot
  +- src
  |   +- docbook
  |   |   +- (editor-quickstart|editor-reference|dev-process|api-quickstart)
  |   |   |   +- 各種ドキュメントのソース
  |   |   +- common
  |   |       +- 各種ドキュメント共通のソース（includeして使う）
  |   +- images
  |   |   +- 画像リソース
  |   +- test
  |       +- java
  |           +- ドキュメント内で使用するソースコード
  + target
      +- (editor-quickstart|editor-reference|dev-process|api-quickstart)
          +- html
          |   +- 複数ページHTML形式ドキュメント
          +- htmlsingle
          |   +- 単一HTML形式ドキュメント
          +- pdf
              +- PDF形式ドキュメント


■ドキュメント記述方法
src/docbook/(editor-quickstart|editor-reference|dev-process|api-quickstart) 内のXMLを編集します。
現在のところ、quickstart 及び editor-reference の２つのドキュメントが定義されていますが、
新たなドキュメントを追加する際は、build.xmlを編集する必要があります。

ドキュメントファイルはベースとなるdocbook-XMLから XInclude によりインクルードされる形になっており、
ファイル単位で再利用が可能です。 <xi:include href="hogehoge.xml"/> のような形でインクルードして
ください。

また、Javaソースコードを引用する際は、src/test/java (mainではない) に配置されたコードを
自動的にドキュメント内に取り込むことができます。（コンパイルされるJavaソースなので、リファクタリングにも
追従し、テストを通すこともできます。）

com.docbook.sample.SampleClass のような記述をすると、target/codes 内にインクルード用の
ドキュメント片が生成されます。 <xi:include href="../../../target/codes/hogehgoe.xml"/>
のような形でインクルードしてください。

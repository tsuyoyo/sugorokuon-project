# README.md

- ラジコンシェルジュ開発メモ

# Googleのコードを見れるopen grok

- [Androidソースコード検索サービス](https://sites.google.com/site/devcollaboration/codesearch)

# Google tab manager

## 導入メモ

- Version2.0にて、いくつかのパラメータ制御のために導入
- https://developers.google.com/tag-manager/android/v4/ を参考に
  - とりあえず1~4を参考にしてContainerからvalueを取れば良さそう
    - https://support.google.com/tagmanager/answer/2644341#ValueCollection
  - 5のdataLayerは、WebのDevGuideには詳細があるが、Androidの方には無いので、別に使わなくても大丈夫そう？
    - Preset Macroの"event"を見ると、dateLayerにEventをセットするとこのvalueに影響がでる?
    - The value is set to "eventNameXYZ" when the following code in your app is executed:
      Android:	dataLayer.push("event", "eventNameXYZ")

# Robolectric

- JVM上でTest (Activityのtestであっても) が動作するとのこと

## 導入メモ

- [Robolectric 公式サイト](http://robolectric.org/)
- [robolectric/robolectric-samples](https://github.com/robolectric/robolectric-samples)
  - 色々Qitaなどで調べたが、このrepositoryの方法でようやく動いた
  - app:build.gradleのcompileSdkVersionに合わせ、repositoryの参照するフォルダを切り替える (Javaのcompile versionが微妙に違う)
  - TestコードのAnnotationの付け方が重要なので、要注意
- [How to setup unit testing in Android Studio](http://www.slideshare.net/tobiaspreuss/how-to-setup-unit-testing-in-android-studio)

### Robolecticを調査したときの参考サイト
- [Android Tools Project Site](http://tools.android.com/)
  - [Unit testing support](http://tools.android.com/tech-docs/unit-testing-support)
  - [Gradle Plugin User Guide](http://tools.android.com/tech-docs/new-build-system/user-guide)
- [Espresso 2.0とRobolectricを併用したテスト環境の作り方](http://qiita.com/Nkzn/items/d5f30bfe31bdf329860b)
- [Gradle使い方メモ](http://qiita.com/opengl-8080/items/4c1aa85b4737bd362d9e)
- [翻訳 android best practice](http://qiita.com/kgmyshin/items/a2358d54ffb3c5435d11)

## 使い方など

- testのコードは、```app/src```の下の```test/java```配下と決まっている (Robolectic plug-inによって)
- Android studio上でUnit testのコードを作る場合は、ASのウインドウの左端にある "Build Variants" から "Test Artifact" をUnit Testsにする
  - これでprojectに組み込まれてdebugも可能になる
  - 逆に組み込まれていないと、赤いJマークがファイルに付く (一応実行はできるがdebuggerが正しく動かない)
- コマンドラインからの実行は ```./gradlew clean check``` もしくは clean 無し

## ハマった

- TestCaseのAnnotationにAndroidManifest.xmlの場所を指定するが、google play serviceを使っているとparseに失敗する
- 3.0-rc2でも直っていないので、下記サイトなどを参考にした
  - https://github.com/robolectric/robolectric/issues/1399

# 左からswipeで出すmenu

## NavigationDrawer
- [Android developer](http://developer.android.com/training/implementing-navigation/nav-drawer.html)
- [drawerアイコンを出す実装サンプル](http://qiita.com/ryugoo/items/1180fae3953891131b92)

# Settings画面
- そもそも何かおかしい
  - [AppCompat v21 — Lollipop 搭載前のデバイスにマテリアル デザインを！](http://googledevjp.blogspot.jp/2014/11/appcompat-v21-lollipop.html)
  - 上記によると、ActionBarActivityとPreferenceFragmentの組み合わせで設定を作ると書いてあるような…?
  - しかしActionBarActivityはappcompat-v21のAPIだが、PreferenceFragmentはその中に無い
  - PreferenceActivityを使う実装だとActionBarが出てこない
    - Kitkatか、どこかのタイミングで出なくなった模様
    - Toolbarでカバーできる？
      - うーん… (ググると出てくるけど、少し無茶してるような)
- 確かにgmailアプリを見てみても、設定画面だけなんかレイアウトが違う
- なので、設定画面はActionBarを出す実装 (Holoテーマ) で行い、ListFragmentとPreferenceFragmentの組み合わせにした

# AsyncTaskLoader
- ここを参考にした
  - [AsyncTaskLoader](http://developer.android.com/reference/android/content/AsyncTaskLoader.html)
  - [AsyncTaskLoaderを使ってみる](http://dev.classmethod.jp/smartphone/asynctaskloader/)
  - [AsyncTaskLoaderを４ヶ月常用してみて](http://nkzn.hatenablog.jp/entry/20120514/1336979844)
- LoaderManagerを取得して、taskのinitを行う実装を行う
  - getLoaderManagerはFragmentにもActivityにも、色々なところにある
  - initの第一引数のIDは、LoaderManagerごとに持っているみたい
  - なので、ActivityからLoaderManagerを取得して複数のFragmentからinitするとIDが衝突したりするので注意
  - ProgramListFragmentではFragmentからLoaderManagerを取った
- LoaderManagerのAPIに関して
  - initLoaderとrestartLoader、どちらを使ってもloaderのloadInBackgroundは走る
  - initLoaderの場合、既にLoaderが存在していると (同じIDのloaderがあると)、第二引数のargsが無視される
  - restartでも、loaderが存在していない場合は自動的に作られるみたい
  - なので今回はrestartを使うことにした (SearchFragment)
  - 参考にしたサイト
    - [LoaderManagerのAPIまとめ](http://yuki312.blogspot.jp/2012/02/loadermanager.html)
    - [initLoader()とrestartLoader()のどちらを使うか](http://d.hatena.ne.jp/Kyakujin/20130528/1369729549)
  
# CardView
- https://guides.codepath.com/android/Using-the-CardView
  
# Search画面
- [【Android】SearchViewを使って検索画面を実装する](http://qiita.com/ryokosuge/items/186c525e0744903ee8ce)
- なぜかgetActionViewがnullを返すところでハマった
  - menuのxmlにてactionViewのclassを指定する所があるが、そこの名前空間をandroidにするとダメらしい
  - [MenuItemCompat.getActionView always returns null](http://stackoverflow.com/questions/18438890/menuitemcompat-getactionview-always-returns-null://qiita.com/ryokosuge/items/186c525e0744903ee8ce)
- [SearchView で検索した後、キーボードを閉じる](http://blog.zaq.ne.jp/oboe2uran/article/990/)

# AppIndexing
- Googleの検索結果にアプリが出てくるようになったらしい
- 時間があったら↓を読んで試してみる
  - [App Indexing でアプリのインストールを増やしましょう](http://googledevjp.blogspot.jp/2015/04/app-indexing.html)
  - [Google 検索用 App Indexing](https://developers.google.com/app-indexing/)

# Material UI

## アイコン
- [ここ](https://github.com/google/material-design-icons)から拝借した (CC)
  - [カタログ](http://google.github.io/material-design-icons/)

## 参考
- [「Material Design」を Android アプリに取り入れよう！](http://qiita.com/suzukihr/items/d12966705e15f1d5f87c)

# Tips

## Fragment

### FragmentからActivityなどへの通知
- [DialogFragment のイベントを Activity とか Fragment に伝えたい。](http://slumbers99.blogspot.jp/2013/11/dialogfragment-activity-fragment.html)
- 今回は、ProgramList->TimeTableFragment->ViewerFragmentへ伝える機構で使っている
  - interfaceをpackage privateにすることで多少はマシかなぁ…。

### Fragment in Fragment
- layout XMLの中、<fragment>の中に<fragment>は書けないらしい…
  - http://developer.android.com/about/versions/android-4.2.html#NestedFragments
  - Note: You cannot inflate a layout into a fragment when that layout includes a <fragment>. Nested fragments are only supported when added to a fragment dynamically.
- backで戻る時に落ちる (inflate失敗などで)

## Service

### onStartCommandの戻り値
- http://d.hatena.ne.jp/adsaria/20100914/1284435095
- START_NOT_STICKY または START_REDELIVER_INTENT は送られてきたコマンドを処理する間だけ実行するService。
- つまり、リクエストされた処理が終わったら、このサービスは割と早いタイミングで死ぬ。

## DLしてきた番組アイコンのMimeType
- 拡張子はmime typeを確認
  - http://stackoverflow.com/questions/9077933/how-to-find-mimetype-of-response
  - [MimeTypeMap#getExtensionFromMimeType(java.lang.String)](http://developer.android.com/reference/android/webkit/MimeTypeMap.html)

# Gradle
- [Gradle Plugin User Guide](http://tools.android.com/tech-docs/new-build-system/user-guide)
- [使えるGradleプロジェクトの作り方](http://www.slideshare.net/MakotoYamazaki/20150425-droidkaigi-gradle)
- [使ってないresourceをビルド時に削除してくれる](https://plus.google.com/+TorNorbye/posts/eHsStybrrBf)

# GenyMotion
- [GenyMotion](https://www.genymotion.com/#!/)
- [Android Studio と Genymotion を使って超爆速なエミュレータ環境を構築する](http://qiita.com/bird_nitryn/items/76e145965e6732352567)

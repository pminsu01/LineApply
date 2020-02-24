# LINE 안드로이드 신입 개발자 메모 어플리케이션 만들기

메모장의 주요 시나리오과 코드의 가독성을 위해 간략한 코드 설명을 위해 쓰게 되었다.
물론 메모장의 주요기능은 기존에 주어진 요구사항을 바탕으로 구현되었다.

<br/>
## 주요 시나리오  
1. 어플리케이션(LineMemo)을 실행 시키면 카메라와 저장소 쓰기/읽기에 대한 권한을 받는다.  minSdkVersion이 21이고 targetSdkVersiond이 27임에 따라 23버전부터 반드시 동의를 얻어야 되기 때문에 스플래시 화면 실행 시 해당 권한을 사용자에게 요청한다.  해당 권한을 거부한다면 어플리케이션은 종료된다.<br/>
2. 메모 리스트 화면이 보여지며 오른쪽 하단의 FloatingActionButton을 통해서 메모를 추가 할 수 있다.<br/>
3. 메모에 들어갈 컨텐츠는 '메모 제목', '메모 내용', '메모 이미지'이다.<br/>
4. 메모 이미지는 세가지 방법으로 첨부 할 수 있다.<br/>
   - 카메라를 이용하여 얻기
      - 스플레시 화면에서 카메라 사용에 대한 권한을 받기 때문에 카메라가 실행이 될 것이며 이에 찍혀진 이미지를 가져온다.
   - 앱 내부 앨범을 이용하여 얻기
      - 앨범 내부 사진 가져오기
   - 외부 링크를 통해 이미지 얻기
      - Glide라는 Google 라이브러리를 사용하였으며 사진형식이 아닐시에는 토스트 메시지와 함께 받아오지 않는다.

<br/>

## 로직 관련

1. Activity
   - MemoDetailReadViewActivity
     - 첫 메모 저장 후 수정하려고 할 때 실행되는 엑티비티
   - MemoDetailViewActivity
     - 첫 메모 작성 및 저장을 위한 엑티비티
   - MemoListViewActivity
     - 스플레시 화면 뒤 나오는 메모 ListView 엑티비티
   - MemoSplashActivity
     - 어플리케이션 실행 시 처음 나오는 엑티비티(권한을 받는다.)
2. Adapter
   - ImageRecyclerViewAdapter
     - 이미지 첨부시 뿌려지는 RecyclerView를 위한 Adapter, 기존 ListView를 활용하려고 했으니 가로 정렬을 보여주는게 Layout이나 사용자에게 더 보기 좋다고 생각되어 가로 정렬을 위해 RecyclerView를 이용하였다.
   - MemoListViewCursorAdapter
     - DB로 부터 읽어온 데이터를 ListView에 분배하기 위한 Adapter, Sqlite는 select시 Cursor 형태로 반환되기 때문에 CursorAdapter를 사용하였다.
3. 기타 Class
   - DataBaseHelper
     - DB 저장, 수정, 삽입을 위한 Class
   - GetImageArrayConvert
     - DB에 저장하기 위해서 ARRAYLIST<Bitmap> 형태를 String값으로 변환하여 DB에 저장하고 반대로 불러올 때는 String 값을 ArrayList 형태로 받아온다.
   - GetImageConver
     - Bitmap to JPG 변환
     - JPG to Bitmap 변환
     - 카메라 사진 Rotate를 위한 로직
   - GetURLImageAsyncTask
     - Glide 라이브러리는 서브 쓰레드를 써야하기 때문에 원래 Thread와 Runnable Handler 등등 고민했지만 많은 데이터를 통신하는 것은 아니기 때문에 쓰레드와 핸들러가 합쳐진 AsyncTask를 사용하였다.
   - ImageItemModel
     - 이미지의 Bitmap와 FileName을 관리하기 위해 사용했다.
   - MemoItemModel
     - DB 저장을 위해서 제목, 세부내용, 작성 시간, 이미지 데이터들을 관리 하기 위해 사용했다.

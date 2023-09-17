package gbsoft.com.dental_gb;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import gbsoft.com.dental_gb.databinding.ActivityRequestImageBinding;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 의뢰서 사진
public class RequestImageActivity extends AppCompatActivity {
    private static final String LOG_TAG2 = "SuccessMessage";    // LOGTAG : 로그 태그
    private static final int MAX = 10;   // 사진 최대 갯수
    private static final int MAX_GALLERY = 5;   // 갤러리 최대 선택 갯수

    // -----------------------------------------------------------------------------------------------------------
    private ActivityRequestImageBinding mBinding;
    // -----------------------------------------------------------------------------------------------------------
    // Adapter
    private RequestImageAdapter mAdapter;    // recyclerView 어뎁터
    // -----------------------------------------------------------------------------------------------------------
    // 공유 변수 (session 기능)
    private String mIp = "";
    private String mId = "";
    private int mCode = -1;
    private String mServerPath = "";
    // -----------------------------------------------------------------------------------------------------------
    // 변수
    private String mRequestCode = "";    // 의뢰코드
    private String mSavedImageList = "";  // 저장된 이미지 리스트
    private String mNewImageList = "";  // 새로운 이미지 리스트
    private File mImage; // 카메라 촬영 이미지 저장
    private String mCurrentPhotoPath;   // 촬영한 이미지 경로
    private ArrayList<String> mUploadFilePath;   // 업로드할 파일 실제 경로, 파일명 리스트
    private ArrayList<Bitmap> mBitmaps_saved;   // 저장된 이미지 리스트
    private ArrayList<Bitmap> mBitmaps_new; // 등록할 이미지 리스트
    private ArrayList<String> mUrls_saved; // 저장된 이미지 이름 리스트
    private ArrayList<String> mUrls_new; // 등록할 이미지 이름 리스트
    private String mBaseMemo = "";   // 기존 메모 저장
    // -----------------------------------------------------------------------------------------------------------

    private RequestManager mGlide; // 카메라 glide

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityRequestImageBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        if (!CommonClass.ic.GetInternet(RequestImageActivity.this.getApplicationContext())) {
            Toast.makeText(RequestImageActivity.this.getApplicationContext(), getString(R.string.internet_check), Toast.LENGTH_LONG).show();
            finish();
        }

        initialSet();

    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void initialSet() {
        SharedPreferences sharedPreferences = getSharedPreferences("auto", Context.MODE_PRIVATE);

        mIp = sharedPreferences.getString("ip", "");
        mId = sharedPreferences.getString("id", "");
        mCode = sharedPreferences.getInt("num", -1);
        mServerPath = sharedPreferences.getString("address", "");

        if (CommonClass.sApiService == null)
            CommonClass.getApiService(mServerPath);

        if (mCode == CommonClass.PDL)
            getShutdownCheck();

        // 6.0 마쉬멜로우 이상일 경우에는 권한 체크 후 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(LOG_TAG2, "권한 설정 완료");
            } else {
                Log.v(LOG_TAG2, "권한 설정 요청");
                ActivityCompat.requestPermissions(RequestImageActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        Intent intent = getIntent();
        mRequestCode = intent.getStringExtra("reqNum");

        mUploadFilePath = new ArrayList<>();
        mBitmaps_saved = new ArrayList<>();
        mBitmaps_new = new ArrayList<>();
        mUrls_saved = new ArrayList<>();
        mUrls_new = new ArrayList<>();

        mGlide = Glide.with(this);

        getImgName();
        getMemo();

        mBinding.btnMemoSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String memo = mBinding.editMemoImage.getText().toString();
                if (!memo.equals(mBaseMemo)) {
                    writeMemo(memo);
                }
            }
        });
    }

    /**
     * 카메라로 찍은 이미지 가져오기
     */
    ActivityResultLauncher<Intent> startCameraActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            mBitmaps_new.clear();
            mUrls_new.clear();
            mUploadFilePath.clear();
//        int savedListCount = mBitmaps_saved.size();
            int savedListCount = mUrls_saved.size();

            if (result.getResultCode() == Activity.RESULT_OK) {
                loadingProgressOpen(); // 로딩창 열기

                mGlide.asBitmap()
                        .load(mCurrentPhotoPath)
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE).skipMemoryCache(true))
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                Log.v("RequestImageActivity_", "Glide Error");
                                if(!RequestImageActivity.this.isFinishing())
                                    CommonClass.showDialog(RequestImageActivity.this, getString(R.string.error_title), getString(R.string.img_error), () -> { }, false);

                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
//                            new_img = resource;
//                            mBitmaps_new.add(compressBitmap(resource, 50));
                                mBitmaps_new.add(resource);
//                            decodeSampledBitmapFromResource(getResources(), , 100, 100)
                                mUploadFilePath.add(mCurrentPhotoPath);
                                try {
                                    uploadImage();
                                } catch (MalformedURLException e) {
                                    Log.e("RequestImageActivity_", "error onActivityResult MalformedURLException");
                                    if(!RequestImageActivity.this.isFinishing())
                                        CommonClass.showDialog(RequestImageActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
//                                if (!IssueManagementDetailActivity.this.isFinishing())
//                                    Common.showDialog(IssueManagementDetailActivity.this, getString(R.string.dialog_error_title), getString(R.string.dialog_catch_error_content), () -> { });
                                }
                                return false;
                            }
                        }).submit();
            } else if(result.getResultCode() == Activity.RESULT_CANCELED) { // 뒤로가기 눌렀을 때
                // 아무일도 일어나지않음
            }
        }
    });


    /**
     * 갤러리에서 선택한 이미지 가져오기
     */
    ActivityResultLauncher<Intent> startGalleryActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            mBitmaps_new.clear();
            mUrls_new.clear();
            mUploadFilePath.clear();
//        int savedListCount = mBitmaps_saved.size();
            int savedListCount = mUrls_saved.size();

            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
//                mRequestCode = data.getStringExtra("reqNum");
                try {
                    // 선택한 데이터가 없을 때
                    if (data.getClipData() == null && data.getData() == null) {
                        Toast.makeText(getApplicationContext(), getString(R.string.none_img_selected), Toast.LENGTH_SHORT).show();
                    } else {
                        if (data.getData() != null) {   // 사진 1장 선택
                            if (savedListCount > MAX + 2) { // 사진 최댓값 넘었을 때
                                Toast.makeText(getApplicationContext(), getString(R.string.max_image1) + MAX + getString(R.string.max_image2), Toast.LENGTH_SHORT).show();
                            } else {    // 정상 동작
                                Uri uri = data.getData();
                                //tempPathInit(uri);
                                galleryBitmap(uri);
                            }
                        } else {    // 사진 여러장 선택
                            ClipData clipData = data.getClipData();
                            int count = clipData.getItemCount();
                            if (count > MAX_GALLERY) {
                                Toast.makeText(getApplicationContext(), getString(R.string.max_once1) + MAX_GALLERY + getString(R.string.max_once2), Toast.LENGTH_SHORT).show();
                            } else {
                                if (count - 2 > MAX - savedListCount) { // 사진 최댓값 넘었을 때
                                    Toast.makeText(getApplicationContext(), getString(R.string.max_image1) + MAX + getString(R.string.max_image2), Toast.LENGTH_SHORT).show();
                                } else {    // 정상 동작
                                    for (int i = 0; i < count; i++) {
                                        Uri uri = clipData.getItemAt(i).getUri();
//                                    tempPathInit(uri);
                                        galleryBitmap(uri, count);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("RequestImageActivity_", "error onActivityResult Exception");
                    if(!RequestImageActivity.this.isFinishing())
                        CommonClass.showDialog(RequestImageActivity.this, getString(R.string.error_title), getString(R.string.img_error), () -> { }, false);
//                if (!RequestImageActivity.this.isFinishing())
//                    Common.showDialog(IssueManagementDetailActivity.this, getString(R.string.dialog_error_title), getString(R.string.dialog_catch_error_content), () -> { });
                }
            } else if(result.getResultCode() == Activity.RESULT_CANCELED) { // 뒤로가기 눌렀을 때
                // 아무일도 일어나지않음
            }
        }
    });

    public String getRealPathFromURI(ContentResolver contentResolver, Uri uri){
        String buildname = Build.MANUFACTURER;
        if(buildname.equals("Xiaomi")){
            return uri.getPath();
        }

        int columnIndex = 0;
        String[] proj = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = contentResolver.query(uri, proj, null, null, null);
        if(cursor.moveToFirst()){
            columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor.getString(columnIndex);
    }

    /**
     * 갤러리에서 한장 가져왔을때
     */
    private void galleryBitmap(Uri uri){
        mGlide
                .asBitmap()
                .load((mCurrentPhotoPath= getRealPathFromURI(getContentResolver(), uri)))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        Log.v("RequestImageActivity_", "gallery bitmap load fail");
                        if(!RequestImageActivity.this.isFinishing())
                            CommonClass.showDialog(RequestImageActivity.this, getString(R.string.error_title), getString(R.string.img_error), () -> { }, false);

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        Log.v("RequestImageActivity_", "gallery bitmap load success");
                        mBitmaps_new.add(resource);
                        mUploadFilePath.add(mCurrentPhotoPath);
                        try {
                                uploadImage();
                        } catch (MalformedURLException e) {
                            Log.e("RequestImageActivity_", "error galleryBitmap MalformedURLException");
                            if(!RequestImageActivity.this.isFinishing())
                                CommonClass.showDialog(RequestImageActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);
//                            if (!IssueManagementDetailActivity.this.isFinishing())
//                                Common.showDialog(IssueManagementDetailActivity.this, getString(R.string.dialog_error_title), getString(R.string.dialog_catch_error_content), () -> { });
                        }
                        return false;
                    }
                }).submit();
    }

    /**
     * 갤러리에서 여러장 가져왔을때
     */
    private void galleryBitmap(Uri uri, int count){
        mGlide
                .asBitmap()
                .load((mCurrentPhotoPath=getRealPathFromURI(getContentResolver(), uri)))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        Log.v("RequestImageActivity_", "gallery bitmap load fail");
                        if(!RequestImageActivity.this.isFinishing())
                            CommonClass.showDialog(RequestImageActivity.this, getString(R.string.error_title), getString(R.string.img_error), () -> { }, false);

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        Log.v("RequestImageActivity_", "gallery bitmap load success");
                        mBitmaps_new.add(resource);
                        mUploadFilePath.add(mCurrentPhotoPath);
                        if(count == mUploadFilePath.size()){
                            try {
                                uploadImage();
                            } catch (MalformedURLException e) {
                                Log.e("RequestImageActivity_", "error galleryBitmap MalformedURLException");
                                if(!RequestImageActivity.this.isFinishing())
                                    CommonClass.showDialog(RequestImageActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { }, false);

//                                if (!IssueManagementDetailActivity.this.isFinishing())
//                                    Common.showDialog(IssueManagementDetailActivity.this, getString(R.string.dialog_error_title), getString(R.string.dialog_catch_error_content), () -> { });
                            }
                        }
                        return false;
                    }
                }).submit();
    }


    /**
     * 이미지 회전2 ( 받아온 degree 만큼 이미지를 회전시킨다)
     */
    public Bitmap getRotateImage(Bitmap src, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    /**
     * bitmap 압축
     */
    private Bitmap compressBitmap(Bitmap bitmap, int rate) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, rate, stream);
        byte[] byteArray = stream.toByteArray();
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * 서버에 이미지 업로드
     */
    private void uploadImage() throws MalformedURLException {
        ArrayList<MultipartBody.Part> files = new ArrayList<>();

        int newItemSize = mBitmaps_new.size();
        int lastItemNum;
        if (mSavedImageList.equals("")) {
            lastItemNum = 1;
        } else {
            String[] a = mSavedImageList.split(",", -1);
            String[] b = a[a.length - 1].split("_");
            String c = b[b.length - 1];
            lastItemNum = (mSavedImageList.equals("")) ? 1 : Integer.parseInt(c) + 1;
        }
        mNewImageList = "";
        int leftLimit = 97; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 5;
        for (int i = 0; i < newItemSize; i++) {
            RequestBody fileBody = RequestBody.create(bitmapToFile(mBitmaps_new.get(i), mUploadFilePath.get(i)), MediaType.parse("multipart/form-data"));
            Random random = new Random();
            String generatedString = random.ints(leftLimit, rightLimit + 1)
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
            String fileName = mRequestCode + "_" + generatedString + "_" + (lastItemNum + i);
            mNewImageList += (mNewImageList.equals("")) ? fileName : "," + fileName;
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("photo", fileName + ".jpg", fileBody);
            files.add(filePart);
        }

        RequestBody id = RequestBody.create(mId, MultipartBody.FORM);
        RequestBody rc = RequestBody.create(mRequestCode, MultipartBody.FORM);
        RequestBody ip = RequestBody.create(mIp, MultipartBody.FORM);

        RequestBody imgName = RequestBody.create((mSavedImageList.equals("")) ? mNewImageList : mSavedImageList + "," + mNewImageList, MultipartBody.FORM);

        Call<ResponseBody> call;
        if(mCode == CommonClass.YK){
            call = CommonClass.sApiService.uploadImagesYK(files, id, rc, imgName, ip);
        } else {
            call = CommonClass.sApiService.uploadImages(files, id, rc, imgName, ip);
        }

        call.enqueue(new Callback<ResponseBody>() {     // 이미지 파일 서버에 전송
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {  // 응답 성공
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        if (result.equals("multer error") || result.equals("unknown error")) {
                            mNewImageList = "";
                            Toast.makeText(RequestImageActivity.this, "error\n: " + result, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RequestImageActivity.this, getString(R.string.registered), Toast.LENGTH_SHORT).show();
                            mSavedImageList += (mSavedImageList.equals("")) ? mNewImageList : "," + mNewImageList;
                            getImgName();
                        }
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!RequestImageActivity.this.isFinishing())
                            CommonClass.showDialog(RequestImageActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { getImgName(); }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!RequestImageActivity.this.isFinishing())
                        CommonClass.showDialog(RequestImageActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { getImgName(); }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!RequestImageActivity.this.isFinishing())
                    CommonClass.showDialog(RequestImageActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { getImgName(); }, false);
            }
        });
    }

    /**
     * Bitmap을 File로 변경
     */
    public File bitmapToFile(Bitmap bitmap, String path) {
        // create a file to write bitmap data
        File file = null;
        try {
            file = new File(path);
            file.createNewFile();

            // Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);  // YOU can also save it in JPEG
            byte[] bitmapdata = bos.toByteArray();

            // write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();

            return saveBitmapToFile(file);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: 파일 처리를 하다가 error가 발생하면 어떻게 조치?(사용자 입장 고려)
        }
        return file;
    }

    public File saveBitmapToFile(File file){
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE=50;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);
            selectedBitmap.recycle();
            return file;
        } catch (Exception e) {
            // TODO: 파일 처리를 하다가 error가 발생하면 어떻게 조치?(사용자 입장 고려)

            return null;
        }
    }

    /**
     * DB에 이미지 리스트 가져오기
     */
    public void getImgName() {
        Call<ResponseBody> call;
        Log.v("getImgName:", mRequestCode + ", " + mId + ", " + mIp + ", " + (mCode == CommonClass.YK) + "");
        if(mCode == CommonClass.YK){
            call = CommonClass.sApiService.getImgNameYK(mRequestCode, mId, mIp);
        } else {
            call = CommonClass.sApiService.getImgName(mRequestCode, mId, mIp);
        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        if (jsonObject.getString("imgName").equals("null") || jsonObject.getString("imgName").equals("")) {
                            mBinding.pvDetail.setImageBitmap(null);
                            Toast.makeText(getApplicationContext(), getString(R.string.no_image_registered), Toast.LENGTH_SHORT).show();
//                                new imageShow(RequestImageActivity.this).execute();
                            imageShow();
                            return;
                        } else {
                            mSavedImageList = jsonObject.getString("imgName");
//                                new imageCheck(RequestImageTestActivity.this).execute();
                            sampleMethod();
                        }
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if(!RequestImageActivity.this.isFinishing())
                            CommonClass.showDialog(RequestImageActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!RequestImageActivity.this.isFinishing())
                            CommonClass.showDialog(RequestImageActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!RequestImageActivity.this.isFinishing())
                        CommonClass.showDialog(RequestImageActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { finish(); }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!RequestImageActivity.this.isFinishing())
                    CommonClass.showDialog(RequestImageActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { finish(); }, false);
            }
        });
    }

    // backgroundTask를 실행하는 메소드
    private void sampleMethod() {
        // task에서 반환할 Hashmap

        //onPreExecute(task 시작 전 실행될 코드 여기에 작성)

        Observable.fromCallable(() -> {
            //doInBackground(task에서 실행할 코드 여기에 작성)
            int responseHttp = 0;
            boolean flag = false;

            try {
                String[] list = mSavedImageList.split(",");

                int list_len = list.length;
                for (int i = 0; i < list_len; i++) {
                    URL url = null;
                    url = new URL(mServerPath + "/uploads_android/" + list[i] + ".jpg");

                    URLConnection connection = url.openConnection();
                    connection.setConnectTimeout(2000);
                    HttpURLConnection httpConnection = (HttpURLConnection) connection;
                    responseHttp = httpConnection.getResponseCode();
                    if (responseHttp == HttpURLConnection.HTTP_OK) {
                        flag = true;
                    } else {
                        flag = false;
                        break;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                if(!RequestImageActivity.this.isFinishing())
                    CommonClass.showDialog(RequestImageActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);

            }
            return flag;
//            return map;

        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

            }

            @Override
            public void onNext(@io.reactivex.rxjava3.annotations.NonNull Boolean aBoolean) {
                if (aBoolean) {
//                new imageShow(RequestImageActivity.this).execute();
                    try {
                        imageShow();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.img_error), Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }


    private void imageShow() throws IOException {
        // 현재 저장되어있는 이미지 초기화
        mUrls_saved.clear();
        addMenu();

        String[] list = mSavedImageList.split(",");
        int list_len = list.length;

        if(list_len == 0) return; // 로딩가능한 이미지가 없음

        for (int i = 0; i < list_len; i++) {
            mUrls_saved.add(list[i]);
        }


        // 현재 저장된 이미지들 adapter에 담고 RecyclerView와 adapter 연결
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RequestImageActivity.this, LinearLayoutManager.HORIZONTAL, false);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mAdapter = new RequestImageAdapter(mUrls_saved, mServerPath, Glide.with(RequestImageActivity.this));
        mAdapter.setOnItemClickListener(new RequestImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                if (pos == 0) { // 갤러리 버튼
                    if (mUrls_saved.size() - 2 == MAX) {
                        Toast.makeText(getApplicationContext(), "사진은 최대 " + MAX + "개까지 가능합니다. 삭제 후 등록하세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                        intent.setData(MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                        startActivityForResult(intent, REQUEST_CODE);
                        startGalleryActivityResult.launch(intent);
                    }
                } else if (pos == 1) {  // 카메라 버튼
                    if (mUrls_saved.size() - 2 == MAX) {
                        Toast.makeText(getApplicationContext(), "사진은 최대 " + MAX + "개까지 가능합니다. 삭제 후 등록하세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        dispatchTakePictureIntent();
                    }
                } else {
                    String url = mAdapter.imgReturnUrl(pos);
                    Glide.with(RequestImageActivity.this)
                            .load(url)
                            .into(mBinding.pvDetail);


//                    Bitmap bitmap = mBitmaps_saved.get(pos);
//                    photo_detail.setImageBitmap(bitmap);

                }
            }
        });

        mAdapter.setOnLongItemClickListener(new RequestImageAdapter.OnLongItemClickListener() {
            @Override
            public void onLongItemClick(int pos) {
                if (pos != 0 && pos != 1) {
                    alertShow(pos - 2);
                }
            }
        });

        mBinding.recyclerView.setAdapter(mAdapter);

        if (mUrls_saved.size() > 2) {
            String url = mAdapter.imgReturnUrl(2);
            Glide.with(RequestImageActivity.this)
                    .load(url)
                    .into(mBinding.pvDetail);
        }
        loadingProgressClose();

    }

    /**
     * 갤러리, 카메라 버튼 추가
     */
    private void addMenu() throws IOException {
        String[] menuPath = {"Gallery", "Camera"};
        int len = menuPath.length;
        for (int i = 0; i < len; i++) {
            mUrls_saved.add(menuPath[i]);

            // 개선 필요

        }
    }

    /**
     * 삭제 다이얼로그 창
     */
    public void alertShow(int pos) {
        CommonClass.showDialog(RequestImageActivity.this, "사진 삭제", "선택된 사진이 삭제됩니다.", () -> {
            String[] list = mSavedImageList.split(",");
            String str_image_list = "";
            int list_len = list.length;
            for (int i = 0; i < list_len; i++) {
                if (i == pos) continue;
                else {
                    str_image_list += (str_image_list.equals("")) ? list[i] : "," + list[i];
                }
            }
            deleteImage(list[pos], str_image_list);

        }, true);
//        AlertDialog.Builder builder = new AlertDialog.Builder(RequestImageActivity.this);
//        builder.setMessage("선택한 사진을 삭제하시겠습니까?");
//        builder.setTitle("사진 삭제").setCancelable(false)
//                .setPositiveButton("예", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String[] list = mSavedImageList.split(",");
//                        String str_image_list = "";
//                        int list_len = list.length;
//                        for (int i = 0; i < list_len; i++) {
//                            if (i == pos) continue;
//                            else {
//                                str_image_list += (str_image_list.equals("")) ? list[i] : "," + list[i];
//                            }
//                        }
//                        try {
//                            deleteImage(list[pos], str_image_list);
//                        } catch (MalformedURLException e) {
//                            e.printStackTrace();
//                        }
//                        dialog.cancel();
//                    }
//                }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        }).setCancelable(false);
//        AlertDialog alert = builder.create();
//        alert.show();
    }

    /**
     * 서버 이미지 삭제
     */
    private void deleteImage(String imageName, String delete_after_image_list) {
        loadingProgressOpen();

        Call<ResponseBody> call;
        if(mCode == CommonClass.YK){
            call = CommonClass.sApiService.deleteImageYK(imageName, mId, mRequestCode, delete_after_image_list, mIp);
        } else {
            call = CommonClass.sApiService.deleteImage(imageName, mId, mRequestCode, delete_after_image_list, mIp);
        }


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        if (result.equals("delete error")) {
                            Toast.makeText(RequestImageActivity.this, result, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RequestImageActivity.this, getString(R.string.removed), Toast.LENGTH_SHORT).show();
                            mSavedImageList = delete_after_image_list;
                            getImgName();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        if(!RequestImageActivity.this.isFinishing())
                            CommonClass.showDialog(RequestImageActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { getImgName(); }, false);
                    }
                } else {
                    Log.v(CommonClass.TAG_ERR, "Fail, " + String.valueOf(response.code()));
                    if(!RequestImageActivity.this.isFinishing())
                        CommonClass.showDialog(RequestImageActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { getImgName(); }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.v(CommonClass.TAG_ERR, "Fail");
                if(!RequestImageActivity.this.isFinishing())
                    CommonClass.showDialog(RequestImageActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { getImgName(); }, false);
            }
        });
    }

    /**
     * 권한 요청
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.v(LOG_TAG2, "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }

    /**
     * 촬영 이미지 파일 생성
     */
    private void createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        mImage = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents(파일 저장 : ACTION_VIEW 인텐트와 함께 사용할 경로)
        mCurrentPhotoPath = mImage.getAbsolutePath();
        Log.i(CommonClass.TAG_ERR, "CurrentPhotoPath : " + mCurrentPhotoPath);
    }

    /**
     * 카메라 촬영 인텐트 전환
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent(인텐트를 처리할 카메라가 있는지 확인)
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go(사진이 들어갈 파일 만들기)
            try {
                createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File(파일 생성중 오류 발생)
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created(파일이 성공적으로 생성된 경우에만 계속)
            if (mImage != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "gbsoft.com.dental_gb.fileprovider",
                        mImage);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                startCameraActivityResult.launch(takePictureIntent);
            }
        }
    }

    /**
     * 로딩창 열기
     * */
    private void loadingProgressOpen(){
        progressDialog = new ProgressDialog(RequestImageActivity.this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCanceledOnTouchOutside(false); // 다이얼로그 밖에 터치 시 종료
        progressDialog.setCancelable(false); // 다이얼로그 취소 가능 (back key)
        progressDialog.show();
    }

    /**
     * 로딩창 닫기
     * */
    public void loadingProgressClose(){
        if(progressDialog == null) return;

        if(progressDialog.isShowing())
            progressDialog.dismiss();
    }

    /**
     * 메모 조회
     */
    private void getMemo() {
        Call<ResponseBody> call;
        if(mCode == CommonClass.YK){
            call = CommonClass.sApiService.getMemoYK(mRequestCode, mId, mIp);
        } else {
            call = CommonClass.sApiService.getMemo(mRequestCode, mId, mIp);
        }


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        mBaseMemo = jsonObject.getString("remark").equals("null") ? "" : jsonObject.getString("remark");
                        mBinding.editMemoImage.setText(mBaseMemo);
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if(!RequestImageActivity.this.isFinishing())
                            CommonClass.showDialog(RequestImageActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if(!RequestImageActivity.this.isFinishing())
                            CommonClass.showDialog(RequestImageActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> { finish(); }, false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!RequestImageActivity.this.isFinishing())
                        CommonClass.showDialog(RequestImageActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> { finish(); }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                loadingProgressClose();
                if(!RequestImageActivity.this.isFinishing())
                    CommonClass.showDialog(RequestImageActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> { finish(); }, false);
            }
        });
    }

    /**
     * 메모 저장
     */
    private void writeMemo(String text) {
        Call<ResponseBody> call;
        if(mCode == CommonClass.YK){
            call = CommonClass.sApiService.writeMemoYK(mRequestCode, text, mId, mIp);
        } else {
            call = CommonClass.sApiService.writeMemo(mRequestCode, text, mId, mIp);
        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.saved), Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if(!RequestImageActivity.this.isFinishing())
                        CommonClass.showDialog(RequestImageActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> {  }, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if(!RequestImageActivity.this.isFinishing())
                    CommonClass.showDialog(RequestImageActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> {  }, false);
            }
        });
    }

    public void getShutdownCheck() {
        Call<ResponseBody> call = CommonClass.sApiService.getShutdownCheck(mId, mIp);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    try {
                        String result = response.body().string();
                        JSONArray jsonArray = new JSONArray(result);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        int mShutdownYn = Integer.parseInt(jsonObject.getString("down"));

                        if (mShutdownYn == 1) {
                            if (!RequestImageActivity.this.isFinishing())
                                CommonClass.showDialog(RequestImageActivity.this, getString(R.string.error_title), getString(R.string.shut_down_on), () -> {
                                    finishAffinity();
                                    System.runFinalization();
                                    System.exit(0);
                                }, false);
                        }
                    } catch (JSONException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: JSON Parsing Error");
                        if (!RequestImageActivity.this.isFinishing())
                            CommonClass.showDialog(RequestImageActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    } catch (IOException e) {
                        Log.e(CommonClass.TAG_ERR, "ERROR: IOException");
                        if (!RequestImageActivity.this.isFinishing())
                            CommonClass.showDialog(RequestImageActivity.this, getString(R.string.error_title), getString(R.string.catch_error_content), () -> finish(), false);
                    }
                } else {
                    Log.d(CommonClass.TAG_ERR, "response is fail, " + response.code());
                    if (!RequestImageActivity.this.isFinishing())
                        CommonClass.showDialog(RequestImageActivity.this, getString(R.string.error_title), getString(R.string.response_error_content), () -> finish(), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(CommonClass.TAG_ERR, "server connect fail");
                if (!RequestImageActivity.this.isFinishing())
                    CommonClass.showDialog(RequestImageActivity.this, getString(R.string.error_title), getString(R.string.connect_error_content), () -> finish(), false);
            }
        });
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Glide.get(this).trimMemory(level);
    }

}
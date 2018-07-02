package com.hanzhuang42.showme.activitys;

import com.hanzhuang42.showme.BuildConfig;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.hanzhuang42.showme.R;
import com.hanzhuang42.showme.fragment.RecyclerViewFragment;
import com.hanzhuang42.showme.util.DbUtility;
import com.hanzhuang42.showme.util.MyToast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int TYPE_DISH = 0;
    private static final int TYPE_CAR = 1;
    private static final int TYPE_LOGO = 2;
    private static final int TYPE_ANIMAL = 3;
    private static final int TYPE_PLANT = 4;
    private int detectType = 0;

    int resume_times = 0;

    private static final int ALL_SIZE = 5;

    private static final String TAG = "MainActivity_Debug";
    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTO = 2;
    private static final int BackToRefresh = 3;

    private DrawerLayout drawerLayout;
    private MaterialViewPager mViewPager;
    private BottomSheetDialog bsd1;

    private RecyclerViewFragment[] fs = new RecyclerViewFragment[ALL_SIZE];
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("");
        initView();
        initBottomDialogView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (resume_times++ != 0) {
            fs[mViewPager.getViewPager().getCurrentItem()].refreshData();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permisions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callAlbum();
                } else {
                    MyToast.showToast(this, "未授予限权！", Toast.LENGTH_SHORT);
                }
        }
    }

    Toolbar toolbar;

    private void initView() {
        drawerLayout = findViewById(R.id.drawer_layout);
        mViewPager = findViewById(R.id.materialViewPager);
        NavigationView navView = findViewById(R.id.nav_view);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(this);

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Intent intent = null;
                Uri uri = null;
                switch (item.getItemId()) {
                    case R.id.nav_img:
                        callAlbum();
                        break;
                    case R.id.nav_camera:
                        callCamera();
                        break;
                    case R.id.nav_del:
                        intent = new Intent(MainActivity.this, DeleteActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_fb:
                        uri = Uri.parse("mailto:hanzhuang42@foxmail.com");
                        String[] email = {"hanzhuang42@foxmail.com"};
                        intent = new Intent(Intent.ACTION_SENDTO, uri);
                        intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
                        intent.putExtra(Intent.EXTRA_SUBJECT, "ShowMe应用用户反馈"); // 主题
                        startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
                        break;
                    case R.id.nav_code:
                        uri = Uri.parse("https://github.com/Miralce42/ShowMe");
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });

        toolbar = mViewPager.getToolbar();
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            }
        }

        mViewPager.getViewPager().setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position % 5) {
                    default:
                        fs[position] = RecyclerViewFragment.newInstance(position);
                        return fs[position];
                }
            }

            @Override
            public int getCount() {
                return 5;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position % 5) {
                    case TYPE_DISH:
                        return "美食";
                    case TYPE_CAR:
                        return "汽车";
                    case TYPE_LOGO:
                        return "商标";
                    case TYPE_ANIMAL:
                        return "动物";
                    case TYPE_PLANT:
                        return "植物";
                }
                return "";
            }
        });

        mViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                detectType = page;
                switch (page) {
                    case TYPE_DISH:
                        return HeaderDesign.fromColorResAndDrawable(
                                R.color.cyan,
                                getResources().getDrawable(R.drawable.wp_3));
                    case TYPE_CAR:
                        return HeaderDesign.fromColorResAndDrawable(
                                R.color.green,
                                getResources().getDrawable(R.drawable.wp_2));
                    case TYPE_LOGO:
                        return HeaderDesign.fromColorResAndDrawable(
                                R.color.blue,
                                getResources().getDrawable(R.drawable.wp_1));
                    case TYPE_ANIMAL:
                        return HeaderDesign.fromColorResAndDrawable(
                                R.color.red,
                                getResources().getDrawable(R.drawable.wp_4));
                    case TYPE_PLANT:
                        return HeaderDesign.fromColorResAndDrawable(
                                R.color.purple,
                                getResources().getDrawable(R.drawable.wp_5));
                }

                //execute others actions if needed (ex : modify your header logo)

                return null;
            }
        });

        mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());

        final View logo = findViewById(R.id.logo_white);
        if (logo != null) {
            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.notifyHeaderChanged();
                    MyToast.showToast(getApplicationContext(), "点击按钮进行图片检测！", Toast.LENGTH_SHORT);
                }
            });
        }
    }

    private void initBottomDialogView() {
        View view = View.inflate(this, R.layout.bottom_dialog, null);
        ImageView image_camera = view.findViewById(R.id.image_camera);
        ImageView image_picture = view.findViewById(R.id.image_picture);
        image_camera.setOnClickListener(this);
        image_picture.setOnClickListener(this);
        bsd1 = new BottomSheetDialog(this);
        bsd1.setContentView(view);
        bsd1.setCancelable(true);
        bsd1.setCanceledOnTouchOutside(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if(checkNetworkState()) {
                    DbUtility.create();
                    bsd1.show();
                }
                break;
            case R.id.image_camera:
                callCamera();
                bsd1.dismiss();
                break;
            case R.id.image_picture:
                callAlbum();
                bsd1.dismiss();
                break;
        }
    }

    public void callCamera() {
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(MainActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                Log.d(TAG, "---------------------------imageUri type is:" + imageUri.getScheme());
                Intent intent = new Intent(MainActivity.this, ShowActivity.class);
                intent.putExtra("uri", imageUri.toString());
                intent.putExtra("detect_type", detectType);
                startActivityForResult(intent, BackToRefresh);
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //判断手机版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitKat(data);
                    } else {
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            case BackToRefresh:
                fs[mViewPager.getViewPager().getCurrentItem()].refreshData();
                break;
        }
    }

    //调用相册内容
    public void callAlbum() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("image/*");
            startActivityForResult(intent, CHOOSE_PHOTO);
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            String id = docId.split(":")[1]; // 解析出数字格式的id
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath, uri); // 根据图片路径显示图片
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath, uri);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath, Uri uri) {
        if (imagePath != null) {
            Intent intent = new Intent(MainActivity.this, ShowActivity.class);
            intent.putExtra("picture_path", imagePath);
            intent.putExtra("uri", uri.toString());
            intent.putExtra("detect_type", detectType);
            startActivityForResult(intent, BackToRefresh);
        } else {
            MyToast.showToast(this, "获取图片失败！", Toast.LENGTH_SHORT);
        }
    }

    private boolean checkNetworkState() {
        boolean flag = false;
        //得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //去进行判断网络是否连接
        if (manager != null &&manager.getActiveNetworkInfo() != null) {
            flag = manager.getActiveNetworkInfo().isAvailable();
        }
        if (!flag) {
            setNetwork();
        }
        return flag;
    }

    /**
     * 网络未连接时，调用设置方法
     */
    private void setNetwork(){
        Toast.makeText(this, "wifi is closed!", Toast.LENGTH_SHORT).show();

        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_AppCompat_Light_Dialog_Alert);
        builder.setTitle("网络提示信息");
        builder.setMessage("网络不可用，如果继续，请先设置网络！");
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = null;
                /**
                 * 判断手机系统的版本！如果API大于10 就是3.0+
                 * 因为3.0以上的版本的设置和3.0以下的设置不一样，调用的方法不同
                 */
                intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create();
        builder.show();
    }
}


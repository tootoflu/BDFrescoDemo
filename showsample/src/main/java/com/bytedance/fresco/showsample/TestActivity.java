package com.bytedance.fresco.showsample;

import android.app.Activity;
import android.view.View;
import android.os.Bundle;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

public class TestActivity extends Activity {

    private SimpleDraweeView mSimpleDraweeView;
    private SimpleDraweeView mSimpleDraweeView1;
    private SimpleDraweeView mSimpleDraweeView2;
    private SimpleDraweeView mSimpleDraweeView3;
    private SimpleDraweeView mSimpleDraweeView4;
    private SimpleDraweeView mSimpleDraweeView5;
    private String mUri = "http://imagex.e7e7e7.com/tos-cn-i-n41c8j48qt/0d21624c8b97415bac449b1b1d6f3c0c.webp~tplv-n41c8j48qt-image.image";
    private String mUri1 = "http://imagex.e7e7e7.com/tos-cn-i-n41c8j48qt/527027808995414aac60c2c2ab94ada7.webp~tplv-n41c8j48qt-image.image";
    private String mUri2 = "http://imagex.e7e7e7.com/tos-cn-i-n41c8j48qt/8d55c68c60d84fd8a30ac4b8484eb768.webp~tplv-n41c8j48qt-image.image";
    private String mUri3 = "http://imagex.e7e7e7.com/tos-cn-i-n41c8j48qt/57e3ced330524cf1b8508c5b0a1d9fb4.webp~tplv-n41c8j48qt-image.image";
    private String mUri4 = "http://imagex.e7e7e7.com/tos-cn-i-n41c8j48qt/cd8379eb37934e499e5a326b0e955a75.webp~tplv-n41c8j48qt-image.image";
    private String mUri5 = "http://imagex.e7e7e7.com/tos-cn-i-n41c8j48qt/63a46ea636f942aaa18a140a02a62e73.webp~tplv-n41c8j48qt-image.image";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mSimpleDraweeView = findViewById(R.id.my_image_view);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(mSimpleDraweeView.getController())
                .setAutoPlayAnimations(true)
                .setUri(mUri)
                .build();
        mSimpleDraweeView.setController(controller);

        mSimpleDraweeView1 = findViewById(R.id.my_image_view1);
        DraweeController controller1 = Fresco.newDraweeControllerBuilder()
                .setOldController(mSimpleDraweeView1.getController())
                .setAutoPlayAnimations(true)
                .setUri(mUri1)
                .build();
        mSimpleDraweeView1.setController(controller1);

        mSimpleDraweeView2 = findViewById(R.id.my_image_view2);
        DraweeController controller2 = Fresco.newDraweeControllerBuilder()
                .setOldController(mSimpleDraweeView2.getController())
                .setAutoPlayAnimations(true)
                .setUri(mUri2)
                .build();
        mSimpleDraweeView2.setController(controller2);

        mSimpleDraweeView3 = findViewById(R.id.my_image_view3);
        DraweeController controller3 = Fresco.newDraweeControllerBuilder()
                .setOldController(mSimpleDraweeView3.getController())
                .setAutoPlayAnimations(true)
                .setUri(mUri3)
                .build();
        mSimpleDraweeView3.setController(controller3);

        mSimpleDraweeView4 = findViewById(R.id.my_image_view4);
        DraweeController controller4 = Fresco.newDraweeControllerBuilder()
                .setOldController(mSimpleDraweeView4.getController())
                .setAutoPlayAnimations(true)
                .setUri(mUri4)
                .build();
        mSimpleDraweeView4.setController(controller4);

        mSimpleDraweeView5 = findViewById(R.id.my_image_view5);
        DraweeController controller5 = Fresco.newDraweeControllerBuilder()
                .setOldController(mSimpleDraweeView5.getController())
                .setAutoPlayAnimations(true)
                .setUri(mUri5)
                .build();
        mSimpleDraweeView5.setController(controller5);
    }

    public void applyNewUri(View view) {
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(mSimpleDraweeView.getController())
                .setAutoPlayAnimations(true)
                .setUri(mUri)
                .build();
        mSimpleDraweeView.setController(controller);
    }

    public void applyNewUri1(View view) {
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(mSimpleDraweeView1.getController())
                .setAutoPlayAnimations(true)
                .setUri(mUri1)
                .build();
        mSimpleDraweeView1.setController(controller);
    }

    public void applyNewUri2(View view) {
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(mSimpleDraweeView2.getController())
                .setAutoPlayAnimations(true)
                .setUri(mUri2)
                .build();
        mSimpleDraweeView2.setController(controller);
    }

    public void applyNewUri3(View view) {
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(mSimpleDraweeView3.getController())
                .setAutoPlayAnimations(true)
                .setUri(mUri3)
                .build();
        mSimpleDraweeView3.setController(controller);
    }

    public void applyNewUri4(View view) {
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(mSimpleDraweeView4.getController())
                .setAutoPlayAnimations(true)
                .setUri(mUri4)
                .build();
        mSimpleDraweeView4.setController(controller);
    }

    public void applyNewUri5(View view) {
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(mSimpleDraweeView5.getController())
                .setAutoPlayAnimations(true)
                .setUri(mUri5)
                .build();
        mSimpleDraweeView5.setController(controller);
    }
}

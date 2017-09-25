package org.artoolkit.ar.samples.ARNative;

import android.widget.FrameLayout;

import org.artoolkit.ar.base.ARActivity;
import org.artoolkit.ar.base.rendering.ARRenderer;

/**
 * Created by erunn on 2017-09-25.
 */

public class AugmentActivity extends ARActivity {
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 133;

    @Override
    protected ARRenderer supplyRenderer() {
        return null;
    }

    @Override
    protected FrameLayout supplyFrameLayout() {
        return null;
    }
}

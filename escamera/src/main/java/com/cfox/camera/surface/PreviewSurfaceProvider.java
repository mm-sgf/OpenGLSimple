package com.cfox.camera.surface;

import android.util.Size;
import android.view.Surface;

public interface PreviewSurfaceProvider {
    Surface getSurface();

    boolean isAvailable();

    Class getPreviewSurfaceClass();

    void setAspectRatio(Size size);
}

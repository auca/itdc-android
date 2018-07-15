package com.toksaitov.doodles;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DrawingView extends View {
    final float BRUSH_WIDTH = 30;

    private Bitmap loadedImage;
    private Bitmap backBuffer;
    private Canvas backBufferCanvas;
    private Paint backBufferPaint;

    private int brushColor;
    private Paint brushPaint;
    private Path brush;

    public DrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        backBufferPaint = new Paint();

        brushColor = Color.argb(255, 255, 0, 0);
        brushPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        brushPaint.setColor(brushColor);
        brushPaint.setStrokeWidth(BRUSH_WIDTH);
        brushPaint.setStrokeJoin(Paint.Join.ROUND);
        brushPaint.setStrokeCap(Paint.Cap.ROUND);
        brushPaint.setStyle(Paint.Style.STROKE);

        brush = new Path();
    }

    public int getBrushColor() {
        return brushColor;
    }

    public void setBrushColor(int brushColor) {
        this.brushColor = brushColor;
        brushPaint.setColor(brushColor);
    }

    public void loadDrawing(File file) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        loadedImage = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        if (loadedImage == null) {
            throw new IOException("Failed to load the file.");
        }
    }

    public void saveDrawing(File file) throws IOException {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            backBuffer.compress(Bitmap.CompressFormat.WEBP, 100, fileOutputStream);
            fileOutputStream.flush();
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (w > 0 && h > 0) {
            backBuffer = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            backBufferCanvas = new Canvas(backBuffer);
            if (loadedImage != null) {
                backBufferCanvas.drawBitmap(loadedImage, 0, 0, backBufferPaint);
            }
        }
    }

    @Override
    protected void onDraw(Canvas frontBufferCanvas) {
        super.onDraw(frontBufferCanvas);

        frontBufferCanvas.drawBitmap(backBuffer, 0, 0, backBufferPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onTouchStarted(event);
                break;
            case MotionEvent.ACTION_MOVE:
                onTouchMoved(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                onTouchEnded();
                break;
        }

        return true;
    }

    private void onTouchStarted(MotionEvent event) {
        brush.moveTo(event.getX(), event.getY());
    }

    private void onTouchMoved(MotionEvent event) {
        final float MIN_DISTANCE = 2;

        float x = event.getX();
        float previousX =
            event.getHistorySize() > 0 ?
                event.getHistoricalX(0):
                x;
        float dx = x - previousX;

        float y = event.getY();
        float previousY =
            event.getHistorySize() > 0 ?
                event.getHistoricalY(0):
                y;
        float dy = y - previousY;

        if (Math.sqrt(dx * dx + dy * dy) > MIN_DISTANCE) {
            brush.quadTo(x + dx * 0.5f, y + dy * 0.5f, x, y);
            backBufferCanvas.drawPath(brush, brushPaint);
            invalidate();
        }

        invalidate();
    }

    private void onTouchEnded() {
        brush.reset();
    }

}

package com.vesit.imaginar.one;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.PixelCopy;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.AugmentedFace;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.AugmentedFaceNode;
import java.util.concurrent.CompletableFuture;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PNGActivity extends AppCompatActivity {


    private Texture faceMeshTexture;
    private final HashMap<AugmentedFace, AugmentedFaceNode> faceNodeMap = new HashMap<>();
    private ModelRenderable faceRegionsRenderable;
    private FaceArFragment arFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_png);

        arFragment = (FaceArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> takePhoto());

        initializeGallery();


    }

    private static final int MASK[] = {

            R.drawable.face_mesh_texture_1,
            R.drawable.face_mesh_texture_2,
            R.drawable.face_mesh_texture_3,
            R.drawable.face_mesh_texture_4,
            R.drawable.face_mesh_texture_5,
            R.drawable.face_mesh_texture_6,
            R.drawable.face_mesh_texture_7,
            R.drawable.face_mesh_texture_8,
            R.drawable.face_mesh_texture_9,
            R.drawable.face_mesh_texture_10

    };

    private void initializeGallery() {
        LinearLayout gallery = findViewById(R.id.gallery_layout);

        ImageView face_mesh = new ImageView(this);
        face_mesh.setImageResource(R.drawable.droid_thumb);
        face_mesh.setContentDescription("fox");
        face_mesh.setOnClickListener(view -> addFilter(0));
        gallery.addView(face_mesh);

        ImageView flower_design = new ImageView(this);
        flower_design.setImageResource(R.drawable.droid_thumb);
        flower_design.setContentDescription("cabin");
        flower_design.setOnClickListener(view -> addFilter(1));
        gallery.addView(flower_design);

        ImageView rose_design = new ImageView(this);
        rose_design.setImageResource(R.drawable.droid_thumb);
        rose_design.setContentDescription("house");
        rose_design.setOnClickListener(view -> addFilter(2));
        gallery.addView(rose_design);

        ImageView brown_makeup = new ImageView(this);
        brown_makeup.setImageResource(R.drawable.droid_thumb);
        brown_makeup.setContentDescription("igloo");
        brown_makeup.setOnClickListener(view -> addFilter(3));
        gallery.addView(brown_makeup);

        ImageView andy = new ImageView(this);
        andy.setImageResource(R.drawable.droid_thumb);
        andy.setContentDescription("fox");
        andy.setOnClickListener(view -> addFilter(4));
        gallery.addView(andy);

        ImageView cabin = new ImageView(this);
        cabin.setImageResource(R.drawable.droid_thumb);
        cabin.setContentDescription("cabin");
        cabin.setOnClickListener(view -> addFilter(5));
        gallery.addView(cabin);

    }

    private void addFilter(int filter_index) {


        ModelRenderable.builder()
                .setSource(this, R.raw.fox_face)
                .build()
                .thenAccept(
                        modelRenderable -> {
                            faceRegionsRenderable = modelRenderable;
                            modelRenderable.setShadowCaster(false);
                            modelRenderable.setShadowReceiver(false);
                        });

        Texture.builder()
                .setSource(this,MASK[filter_index])
                .build()
                .thenAccept(texture -> faceMeshTexture = texture);

        ArSceneView sceneView = arFragment.getArSceneView();
        sceneView.setCameraStreamRenderPriority(Renderable.RENDER_PRIORITY_FIRST);
        Scene scene = sceneView.getScene();


        Collection<AugmentedFace> faceList =
                sceneView.getSession().getAllTrackables(AugmentedFace.class);

        for (AugmentedFace face : faceList) {
            AugmentedFaceNode faceNode = new AugmentedFaceNode(face);


            faceNode.setParent(scene);

            faceNode.setFaceMeshTexture(faceMeshTexture);
        }


        scene.addOnUpdateListener(

                (FrameTime frameTime) -> {
                    if (faceRegionsRenderable == null || faceMeshTexture == null) {
                        return;
                    }



                    for (AugmentedFace face : faceList) {
                        if (!faceNodeMap.containsKey(face)) {
                            AugmentedFaceNode faceNode = new AugmentedFaceNode(face);
                            faceNode.setParent(scene);
                            faceNode.setFaceMeshTexture(faceMeshTexture);
//                            faceNode.setFaceRegionsRenderable(faceRegionsRenderable);
                            faceNodeMap.put(face, faceNode);
                        }
                    }

                    Iterator<Map.Entry<AugmentedFace, AugmentedFaceNode>> iter = faceNodeMap.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry<AugmentedFace, AugmentedFaceNode> entry = iter.next();
                        AugmentedFace face = entry.getKey();
                        if (face.getTrackingState() == TrackingState.STOPPED) {
                            AugmentedFaceNode faceNode = entry.getValue();
                            faceNode.setParent(null);
                            iter.remove();
                        }
                    }
                });


    }




    private String generateFilename() {
        String date =
                new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault()).format(new Date());
        return Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + File.separator + "Sceneform/" + date + "_screenshot.jpg";
    }

    private void saveBitmapToDisk(Bitmap bitmap, String filename) throws IOException {

        File out = new File(filename);
        if (!out.getParentFile().exists()) {
            out.getParentFile().mkdirs();
        }
        try (FileOutputStream outputStream = new FileOutputStream(filename);
             ByteArrayOutputStream outputData = new ByteArrayOutputStream()) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputData);
            outputData.writeTo(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException ex) {
            throw new IOException("Failed to save bitmap to disk", ex);
        }
    }

    private void takePhoto() {
        final String filename = generateFilename();
        ArSceneView view = arFragment.getArSceneView();

        // Create a bitmap the size of the scene view.
        final Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.ARGB_8888);

        // Create a handler thread to offload the processing of the image.
        final HandlerThread handlerThread = new HandlerThread("PixelCopier");
        handlerThread.start();
        // Make the request to copy.
        PixelCopy.request(view, bitmap, (copyResult) -> {
            if (copyResult == PixelCopy.SUCCESS) {
                try {
                    saveBitmapToDisk(bitmap, filename);
                } catch (IOException e) {
                    Toast toast = Toast.makeText(PNGActivity.this, e.toString(),
                            Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                        "Photo saved", Snackbar.LENGTH_LONG);
                snackbar.setAction("Open in Photos", v -> {
                    File photoFile = new File(filename);

                    Uri photoURI = FileProvider.getUriForFile(PNGActivity.this,
                            PNGActivity.this.getPackageName() + ".ar.codelab.name.provider",
                            photoFile);
                    Intent intent = new Intent(Intent.ACTION_VIEW, photoURI);
                    intent.setDataAndType(photoURI, "image/*");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);

                });
                snackbar.show();
            } else {
                Toast toast = Toast.makeText(PNGActivity.this,
                        "Failed to copyPixels: " + copyResult, Toast.LENGTH_LONG);
                toast.show();
            }
            handlerThread.quitSafely();
        }, new Handler(handlerThread.getLooper()));
    }
}
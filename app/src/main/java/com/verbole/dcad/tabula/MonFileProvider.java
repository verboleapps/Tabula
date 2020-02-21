package com.verbole.dcad.tabula;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URLConnection;

// cf livre sur android busycoders
//https://github.com/commonsguy/cw-omnibus/blob/master/ContentProvider/V4FileProvider/app/src/main/java/com/commonsware/android/cp/v4file/LegacyCompatFileProvider.java

public class MonFileProvider extends FileProvider {



    private final static String[] OPENABLE_PROJECTION= {
            OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE };

    public static final Uri CONTENT_URI = Uri.parse("content://com.verbole.dcad.tabula.provider/tabula/");
    // tabula en minuscule car ds filepath le nom est en minuscule
    // va renvoyer external storage + dossier Tabula



    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode)
            throws FileNotFoundException {
        File root=getContext().getFilesDir();
        File f=new File(root, uri.getPath()).getAbsoluteFile();

        if (!f.getPath().startsWith(root.getPath())) {
            throw new
                    SecurityException("Resolved path jumped beyond root");
        }

        if (f.exists()) {
            return(ParcelFileDescriptor.open(f, parseMode(mode)));
        }

        throw new FileNotFoundException(uri.getPath());
    }

    protected long getDataLength(Uri uri) {
        File f=new File(getContext().getFilesDir(), uri.getPath());

        return(f.length());
    }

    // following is from ParcelFileDescriptor source code
    // Copyright (C) 2006 The Android Open Source Project
    // (even though this method was added much after 2006...)

    private static int parseMode(String mode) {
        final int modeBits;
        if ("r".equals(mode)) {
            modeBits= ParcelFileDescriptor.MODE_READ_ONLY;
        }
        else if ("w".equals(mode) || "wt".equals(mode)) {
            modeBits=
                    ParcelFileDescriptor.MODE_WRITE_ONLY
                            | ParcelFileDescriptor.MODE_CREATE
                            | ParcelFileDescriptor.MODE_TRUNCATE;
        }
        else if ("wa".equals(mode)) {
            modeBits=
                    ParcelFileDescriptor.MODE_WRITE_ONLY
                            | ParcelFileDescriptor.MODE_CREATE
                            | ParcelFileDescriptor.MODE_APPEND;
        }
        else if ("rw".equals(mode)) {
            modeBits=
                    ParcelFileDescriptor.MODE_READ_WRITE
                            | ParcelFileDescriptor.MODE_CREATE;
        }
        else if ("rwt".equals(mode)) {
            modeBits=
                    ParcelFileDescriptor.MODE_READ_WRITE
                            | ParcelFileDescriptor.MODE_CREATE
                            | ParcelFileDescriptor.MODE_TRUNCATE;
        }
        else {
            throw new IllegalArgumentException("Bad mode '" + mode + "'");
        }
        return modeBits;
    }

    /*
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return(new FPCursorWrapper(super.query(uri, projection, selection, selectionArgs, sortOrder)));
    }
     */

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        if (projection == null) {
            projection=OPENABLE_PROJECTION;
        }

        final MatrixCursor cursor=new MatrixCursor(projection, 1);

        MatrixCursor.RowBuilder b=cursor.newRow();

        for (String col : projection) {
            if (OpenableColumns.DISPLAY_NAME.equals(col)) {
                b.add(getFileName(uri));
            }
            else if (OpenableColumns.SIZE.equals(col)) {
                b.add(getDataLength(uri));
            }
            else { // unknown, so just add null
                b.add(null);
            }
        }

        return(new FPCursorWrapper(cursor));
    }

    @Override
    public String getType(Uri uri) {
        return(URLConnection.guessContentTypeFromName(uri.toString()));
    }

    protected String getFileName(Uri uri) {
        return(uri.getLastPathSegment());
    }


}

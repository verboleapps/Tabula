package com.verbole.dcad.tabula;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.DocumentsProvider;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class MonDocumentProvider extends DocumentsProvider {

    private static final String ROOT_ID = "Tabula";
    private static final String ROOT_DOCUMENT_ID = "Tabula";

    private static final String[] DEFAULT_ROOT_PROJECTION = new String[]{
            DocumentsContract.Root.COLUMN_ROOT_ID,
            DocumentsContract.Root.COLUMN_MIME_TYPES,
            DocumentsContract.Root.COLUMN_FLAGS,
            DocumentsContract.Root.COLUMN_ICON,
            DocumentsContract.Root.COLUMN_TITLE,
            DocumentsContract.Root.COLUMN_SUMMARY,
            DocumentsContract.Root.COLUMN_DOCUMENT_ID,
            DocumentsContract.Root.COLUMN_AVAILABLE_BYTES
    };

    // Use these as the default columns to return information about a document if no specific
    // columns are requested in a query.
    private static final String[] DEFAULT_DOCUMENT_PROJECTION = new String[]{
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_MIME_TYPE,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_LAST_MODIFIED,
            DocumentsContract.Document.COLUMN_FLAGS,
            DocumentsContract.Document.COLUMN_SIZE
    };

    private static final String[] SUPPORTED_ROOT_PROJECTION = new String[]{
            DocumentsContract.Root.COLUMN_ROOT_ID,
            DocumentsContract.Root.COLUMN_MIME_TYPES,
            DocumentsContract.Root.COLUMN_ICON,
            DocumentsContract.Root.COLUMN_FLAGS,

            DocumentsContract.Root.COLUMN_TITLE,
            DocumentsContract.Root.COLUMN_DOCUMENT_ID


    };

    // No official policy on how many to return, but make sure you do limit the number of recent
    // and search results.
    private static final int MAX_SEARCH_RESULTS = 20;
    private static final int MAX_LAST_MODIFIED = 5;

    private static final String ROOT = "root";
    private File mBaseDir;
    private final String TAG = "MonDocProvider ";

    @Override
    public boolean onCreate() {
        Log.d(ActivitePrincipale2.TAG,TAG + "on create path ??? ");
        mBaseDir = getContext().getExternalFilesDir(null);
        Log.d(ActivitePrincipale2.TAG,TAG + "on create path : " + mBaseDir.getAbsolutePath());
        return true;
    }

    private static String[] netProjection(String[] requested, String[] supported) {
        if (requested == null) {
            return supported;
        }
        ArrayList<String> result = new ArrayList<String>();
        for (String request : requested) {
            for (String support : supported) {
                if (request.equals(support)) {
                    result.add(request);
                    break;
                }
            }
        }
        return result.toArray(new String[0]);
    }




    @Override
    public Cursor queryRoots(String[] projection) throws FileNotFoundException {
        Log.d(ActivitePrincipale2.TAG, TAG + "query roots");
        // Use a MatrixCursor to build a cursor
        // with either the requested fields, or the default
        // projection if "projection" is null.

        final MatrixCursor result =
                new MatrixCursor(resolveRootProjection(projection));

        /*
        String[] netProjection1 = netProjection(projection,SUPPORTED_ROOT_PROJECTION);
        MatrixCursor result = new MatrixCursor(netProjection1);
*/


        // It's possible to have multiple roots (e.g. for multiple accounts in the
        // same app) -- just add multiple cursor rows.
        final MatrixCursor.RowBuilder row = result.newRow();

        /*
        row.add(DocumentsContract.Root.COLUMN_ROOT_ID, ROOT_ID);
        row.add(DocumentsContract.Root.COLUMN_FLAGS,DocumentsContract.Root.FLAG_LOCAL_ONLY);
        row.add(DocumentsContract.Root.COLUMN_TITLE,"yo docs");
        row.add(DocumentsContract.Root.COLUMN_DOCUMENT_ID,ROOT_DOCUMENT_ID);
        */
        // You can provide an optional summary, which helps distinguish roots
        // with the same title. You can also use this field for displaying an
        // user account name.
        row.add(DocumentsContract.Root.COLUMN_SUMMARY, "yo colonne"); //getContext().getString(R.string.root_summary)

        // FLAG_SUPPORTS_CREATE means at least one directory under the root supports
        // creating documents. FLAG_SUPPORTS_RECENTS means your application's most
        // recently used documents will show up in the "Recents" category.
        // FLAG_SUPPORTS_SEARCH allows users to search all documents the application
        // shares.
        row.add(DocumentsContract.Root.COLUMN_FLAGS, DocumentsContract.Root.FLAG_SUPPORTS_CREATE |
                DocumentsContract.Root.FLAG_SUPPORTS_RECENTS |
                DocumentsContract.Root.FLAG_SUPPORTS_SEARCH);

        // COLUMN_TITLE is the root title (e.g. Gallery, Drive).
        row.add(DocumentsContract.Root.COLUMN_TITLE, "yo titre"); // getContext().getString(R.string.title)



        // This document id cannot change after it's shared.
        row.add(DocumentsContract.Root.COLUMN_DOCUMENT_ID, getDocIdForFile(mBaseDir));

        // The child MIME types are used to filter the roots and only present to the
        // user those roots that contain the desired type somewhere in their file hierarchy.
        row.add(DocumentsContract.Root.COLUMN_MIME_TYPES, getChildMimeTypes(mBaseDir));
        row.add(DocumentsContract.Root.COLUMN_AVAILABLE_BYTES, mBaseDir.getFreeSpace());
        row.add(DocumentsContract.Root.COLUMN_ICON, R.drawable.ic_infos);

        return result;
    }

    @Override
    public Cursor queryDocument(String documentId, String[] projection) throws FileNotFoundException {
        Log.d(ActivitePrincipale2.TAG, TAG + "query docs");
        /*
        final MatrixCursor result = new
                MatrixCursor(resolveDocumentProjection(projection));
        includeFile(result, documentId, null);

         */

        String[] netProjection1 = netProjection(projection,SUPPORTED_ROOT_PROJECTION);
        MatrixCursor result = new MatrixCursor(netProjection1);
        try {
            addDocumentRow(result, Uri.parse(documentId).getLastPathSegment(),getFileForDocId(documentId));
        } catch (IOException e) {
            Log.d(ActivitePrincipale2.TAG,TAG + "erreur query doc : " + e.toString());
        }

        return result;
    }

    @Override
    public Cursor queryChildDocuments(String parentDocumentId, String[] projection, String sortOrder) throws FileNotFoundException {
        Log.d(ActivitePrincipale2.TAG, TAG + "query child doc");

        /*
        final MatrixCursor result = new
                MatrixCursor(resolveDocumentProjection(projection));
        final File parent = getFileForDocId(parentDocumentId);
        for (File file : parent.listFiles()) {
            // Adds the file's display name, MIME type, size, and so on.
            includeFile(result, null, file);
        }
        return result;

         */

        String[] netProjection1 = netProjection(projection,SUPPORTED_ROOT_PROJECTION);
        MatrixCursor result = new MatrixCursor(netProjection1);
        try {
            final File parent = getFileForDocId(parentDocumentId);
            for (File file : parent.listFiles()) {
                // Adds the file's display name, MIME type, size, and so on.
                addDocumentRow(result, null, file);
            }
        } catch (IOException e) {
            Log.d(ActivitePrincipale2.TAG,TAG + "except child docs " + e.toString());
        }
        return result;
    }

    private void addDocumentRow(MatrixCursor result,String child, File file) throws IOException {
        MatrixCursor.RowBuilder row = result.newRow();
        row.add(DocumentsContract.Document.COLUMN_DOCUMENT_ID,getDocIdForFile(file));
        row.add(DocumentsContract.Document.COLUMN_MIME_TYPE,getTypeForFile(file));
        row.add(DocumentsContract.Document.COLUMN_FLAGS,0);
    }

/*
    @Override
    public AssetFileDescriptor openDocumentThumbnail(String documentId, Point sizeHint,
                                                     CancellationSignal signal)
            throws FileNotFoundException {

        final File file = getThumbnailFileForDocId(documentId);
        final ParcelFileDescriptor pfd =
                ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        return new AssetFileDescriptor(pfd, 0, AssetFileDescriptor.UNKNOWN_LENGTH);
    }
*/
    @Override
    public ParcelFileDescriptor openDocument(String documentId, String mode, @Nullable CancellationSignal signal) throws FileNotFoundException {
        Log.d(ActivitePrincipale2.TAG, "openDocument, mode: " + mode);
        // It's OK to do network operations in this method to download the document,
        // as long as you periodically check the CancellationSignal. If you have an
        // extremely large file to transfer from the network, a better solution may
        // be pipes or sockets (see ParcelFileDescriptor for helper methods).

        final File file = getFileForDocId(documentId);
        final int accessMode = ParcelFileDescriptor.parseMode(mode);

        final boolean isWrite = (mode.indexOf('w') != -1);
        if(isWrite) {
            // Attach a close listener if the document is opened in write mode.
            try {
                Handler handler = new Handler(getContext().getMainLooper());
                return ParcelFileDescriptor.open(file, accessMode, handler,
                        new ParcelFileDescriptor.OnCloseListener() {
                            @Override
                            public void onClose(IOException e) {

                                // Update the file with the cloud server. The client is done
                                // writing.
                                Log.d(ActivitePrincipale2.TAG, "A file with id " +
                                        documentId + " has been closed! Time to " +
                                        "update the server.");
                            }

                        });
            } catch (IOException e) {
                throw new FileNotFoundException("Failed to open document with id"
                        + documentId + " and mode " + mode);
            }
        } else {
            return ParcelFileDescriptor.open(file, accessMode);
        }
    }




    /**
     * @param projection the requested root column projection
     * @return either the requested root column projection, or the default projection if the
     * requested projection is null.
     */
    private static String[] resolveRootProjection(String[] projection) {
        return projection != null ? projection : DEFAULT_ROOT_PROJECTION;
    }

    private static String[] resolveDocumentProjection(String[] projection) {
        return projection != null ? projection : DEFAULT_DOCUMENT_PROJECTION;
    }

    /**
     * Get a file's MIME type
     *
     * @param file the File object whose type we want
     * @return the MIME type of the file
     */
    private static String getTypeForFile(File file) {
        if (file.isDirectory()) {
            return DocumentsContract.Document.MIME_TYPE_DIR;
        } else {
            return getTypeForName(file.getName());
        }
    }

    /**
     * Get the MIME data type of a document, given its filename.
     *
     * @param name the filename of the document
     * @return the MIME data type of a document
     */
    private static String getTypeForName(String name) {
        final int lastDot = name.lastIndexOf('.');
        if (lastDot >= 0) {
            final String extension = name.substring(lastDot + 1);
            final String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            if (mime != null) {
                return mime;
            }
        }
        return "application/octet-stream";
    }

    /**
     * Gets a string of unique MIME data types a directory supports, separated by newlines.  This
     * should not change.
     *
     * @param parent the File for the parent directory
     * @return a string of the unique MIME data types the parent directory supports
     */
    private String getChildMimeTypes(File parent) {
        Set<String> mimeTypes = new HashSet<String>();
        mimeTypes.add("image/*");
        mimeTypes.add("text/*");
        mimeTypes.add("application/vnd.openxmlformats-officedocument.wordprocessingml.document");

        // Flatten the list into a string and insert newlines between the MIME type strings.
        StringBuilder mimeTypesString = new StringBuilder();
        for (String mimeType : mimeTypes) {
            mimeTypesString.append(mimeType).append("\n");
        }

        return mimeTypesString.toString();
    }

    /**
     * Get the document ID given a File.  The document id must be consistent across time.  Other
     * applications may save the ID and use it to reference documents later.
     * <p/>
     * This implementation is specific to this demo.  It assumes only one root and is built
     * directly from the file structure.  However, it is possible for a document to be a child of
     * multiple directories (for example "android" and "images"), in which case the file must have
     * the same consistent, unique document ID in both cases.
     *
     * @param file the File whose document ID you want
     * @return the corresponding document ID
     */
    private String getDocIdForFile(File file) {
        String path = file.getAbsolutePath();

        // Start at first char of path under root
        final String rootPath = mBaseDir.getPath();
        if (rootPath.equals(path)) {
            path = "";
        } else if (rootPath.endsWith("/")) {
            path = path.substring(rootPath.length());
        } else {
            path = path.substring(rootPath.length() + 1);
        }

        return "root" + ':' + path;
    }

    /**
     * Add a representation of a file to a cursor.
     *
     * @param result the cursor to modify
     * @param docId  the document ID representing the desired file (may be null if given file)
     * @param file   the File object representing the desired file (may be null if given docID)
     * @throws java.io.FileNotFoundException
     */
    private void includeFile(MatrixCursor result, String docId, File file)
            throws FileNotFoundException {
        if (docId == null) {
            docId = getDocIdForFile(file);
        } else {
            file = getFileForDocId(docId);
        }

        int flags = 0;

        if (file.isDirectory()) {
            // Request the folder to lay out as a grid rather than a list. This also allows a larger
            // thumbnail to be displayed for each image.
            //            flags |= Document.FLAG_DIR_PREFERS_GRID;

            // Add FLAG_DIR_SUPPORTS_CREATE if the file is a writable directory.
            if (file.isDirectory() && file.canWrite()) {
                flags |= DocumentsContract.Document.FLAG_DIR_SUPPORTS_CREATE;
            }
        } else if (file.canWrite()) {
            // If the file is writable set FLAG_SUPPORTS_WRITE and
            // FLAG_SUPPORTS_DELETE
            flags |= DocumentsContract.Document.FLAG_SUPPORTS_WRITE;
            flags |= DocumentsContract.Document.FLAG_SUPPORTS_DELETE;
        }

        final String displayName = file.getName();
        final String mimeType = getTypeForFile(file);

        if (mimeType.startsWith("image/")) {
            // Allow the image to be represented by a thumbnail rather than an icon
            flags |= DocumentsContract.Document.FLAG_SUPPORTS_THUMBNAIL;
        }

        final MatrixCursor.RowBuilder row = result.newRow();
        row.add(DocumentsContract.Document.COLUMN_DOCUMENT_ID, docId);
        row.add(DocumentsContract.Document.COLUMN_DISPLAY_NAME, displayName);
        row.add(DocumentsContract.Document.COLUMN_SIZE, file.length());
        row.add(DocumentsContract.Document.COLUMN_MIME_TYPE, mimeType);
        row.add(DocumentsContract.Document.COLUMN_LAST_MODIFIED, file.lastModified());
        row.add(DocumentsContract.Document.COLUMN_FLAGS, flags);

        // Add a custom icon
 //       row.add(DocumentsContract.Document.COLUMN_ICON, R.drawable.ic_launcher);
    }

    /**
     * Translate your custom URI scheme into a File object.
     *
     * @param docId the document ID representing the desired file
     * @return a File represented by the given document ID
     * @throws java.io.FileNotFoundException
     */
    private File getFileForDocId(String docId) throws FileNotFoundException {
        File target = mBaseDir;
        if (docId.equals(ROOT)) {
            return target;
        }
        final int splitIndex = docId.indexOf(':', 1);
        if (splitIndex < 0) {
            throw new FileNotFoundException("Missing root for " + docId);
        } else {
            final String path = docId.substring(splitIndex + 1);
            target = new File(target, path);
            if (!target.exists()) {
                throw new FileNotFoundException("Missing file for " + docId + " at " + target);
            }
            return target;
        }
    }
}

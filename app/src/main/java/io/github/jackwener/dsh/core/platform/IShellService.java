package io.github.jackwener.dsh.core.platform;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.os.RemoteException;

/* JADX INFO: loaded from: /data/user/0/io.github.jackwener.dsh/files/home/apk_x/classes3.dex */
public interface IShellService extends IInterface {
    public static final String DESCRIPTOR = "io.github.jackwener.dsh.core.platform.IShellService";

    public static class Default implements IShellService {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // io.github.jackwener.dsh.core.platform.IShellService
        public byte[] captureDisplay(int displayId, int maxWidth) throws RemoteException {
            return null;
        }

        @Override // io.github.jackwener.dsh.core.platform.IShellService
        public int createDisplay(int width, int height, int dpi) throws RemoteException {
            return 0;
        }

        @Override // io.github.jackwener.dsh.core.platform.IShellService
        public void destroy() throws RemoteException {
        }

        @Override // io.github.jackwener.dsh.core.platform.IShellService
        public int displayFlags(int displayId) throws RemoteException {
            return 0;
        }

        @Override // io.github.jackwener.dsh.core.platform.IShellService
        public String exec(String cmd, long timeoutMs) throws RemoteException {
            return null;
        }

        @Override // io.github.jackwener.dsh.core.platform.IShellService
        public int frameBlank(int displayId) throws RemoteException {
            return 0;
        }

        @Override // io.github.jackwener.dsh.core.platform.IShellService
        public String fsDelete(String path) throws RemoteException {
            return null;
        }

        @Override // io.github.jackwener.dsh.core.platform.IShellService
        public String fsList(String path, boolean recursive, int limit) throws RemoteException {
            return null;
        }

        @Override // io.github.jackwener.dsh.core.platform.IShellService
        public String fsMkdirs(String path) throws RemoteException {
            return null;
        }

        @Override // io.github.jackwener.dsh.core.platform.IShellService
        public String fsMove(String from, String to, boolean copy) throws RemoteException {
            return null;
        }

        @Override // io.github.jackwener.dsh.core.platform.IShellService
        public ParcelFileDescriptor fsOpen(String path, String mode) throws RemoteException {
            return null;
        }

        @Override // io.github.jackwener.dsh.core.platform.IShellService
        public String fsSearch(String dir, String nameGlob, String content, boolean regex, int limit, String sort) throws RemoteException {
            return null;
        }

        @Override // io.github.jackwener.dsh.core.platform.IShellService
        public String fsStat(String path) throws RemoteException {
            return null;
        }

        @Override // io.github.jackwener.dsh.core.platform.IShellService
        public String lastDisplayError() throws RemoteException {
            return null;
        }

        @Override // io.github.jackwener.dsh.core.platform.IShellService
        public boolean moveTask(int taskId, int displayId) throws RemoteException {
            return false;
        }

        @Override // io.github.jackwener.dsh.core.platform.IShellService
        public void releaseDisplay(int displayId) throws RemoteException {
        }

        @Override // io.github.jackwener.dsh.core.platform.IShellService
        public void restoreAudio() throws RemoteException {
        }

        @Override // io.github.jackwener.dsh.core.platform.IShellService
        public boolean setAudioMuted(String pkg, boolean muted) throws RemoteException {
            return false;
        }

        @Override // io.github.jackwener.dsh.core.platform.IShellService
        public String tasks() throws RemoteException {
            return null;
        }

        @Override // io.github.jackwener.dsh.core.platform.IShellService
        public String trustedDisplayDenied() throws RemoteException {
            return null;
        }

        @Override // io.github.jackwener.dsh.core.platform.IShellService
        public boolean wakeDisplay(int displayId) throws RemoteException {
            return false;
        }
    }

    byte[] captureDisplay(int displayId, int maxWidth) throws RemoteException;

    int createDisplay(int width, int height, int dpi) throws RemoteException;

    void destroy() throws RemoteException;

    int displayFlags(int displayId) throws RemoteException;

    String exec(String cmd, long timeoutMs) throws RemoteException;

    int frameBlank(int displayId) throws RemoteException;

    String fsDelete(String path) throws RemoteException;

    String fsList(String path, boolean recursive, int limit) throws RemoteException;

    String fsMkdirs(String path) throws RemoteException;

    String fsMove(String from, String to, boolean copy) throws RemoteException;

    ParcelFileDescriptor fsOpen(String path, String mode) throws RemoteException;

    String fsSearch(String dir, String nameGlob, String content, boolean regex, int limit, String sort) throws RemoteException;

    String fsStat(String path) throws RemoteException;

    String lastDisplayError() throws RemoteException;

    boolean moveTask(int taskId, int displayId) throws RemoteException;

    void releaseDisplay(int displayId) throws RemoteException;

    void restoreAudio() throws RemoteException;

    boolean setAudioMuted(String pkg, boolean muted) throws RemoteException;

    String tasks() throws RemoteException;

    String trustedDisplayDenied() throws RemoteException;

    boolean wakeDisplay(int displayId) throws RemoteException;

    public static abstract class Stub extends Binder implements IShellService {
        static final int TRANSACTION_captureDisplay = 5;
        static final int TRANSACTION_createDisplay = 3;
        static final int TRANSACTION_destroy = 16777115;
        static final int TRANSACTION_displayFlags = 20;
        static final int TRANSACTION_exec = 2;
        static final int TRANSACTION_frameBlank = 6;
        static final int TRANSACTION_fsDelete = 17;
        static final int TRANSACTION_fsList = 13;
        static final int TRANSACTION_fsMkdirs = 15;
        static final int TRANSACTION_fsMove = 16;
        static final int TRANSACTION_fsOpen = 14;
        static final int TRANSACTION_fsSearch = 18;
        static final int TRANSACTION_fsStat = 12;
        static final int TRANSACTION_lastDisplayError = 11;
        static final int TRANSACTION_moveTask = 8;
        static final int TRANSACTION_releaseDisplay = 4;
        static final int TRANSACTION_restoreAudio = 10;
        static final int TRANSACTION_setAudioMuted = 9;
        static final int TRANSACTION_tasks = 7;
        static final int TRANSACTION_trustedDisplayDenied = 21;
        static final int TRANSACTION_wakeDisplay = 19;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IShellService.DESCRIPTOR);
        }

        public static IShellService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = obj.queryLocalInterface(IShellService.DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IShellService)) {
                return (IShellService) iInterfaceQueryLocalInterface;
            }
            return new Proxy(obj);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IShellService.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(IShellService.DESCRIPTOR);
                return true;
            }
            if (i == TRANSACTION_destroy) {
                destroy();
                parcel2.writeNoException();
            } else {
                switch (i) {
                    case 2:
                        String strExec = exec(parcel.readString(), parcel.readLong());
                        parcel2.writeNoException();
                        parcel2.writeString(strExec);
                        break;
                    case 3:
                        int iCreateDisplay = createDisplay(parcel.readInt(), parcel.readInt(), parcel.readInt());
                        parcel2.writeNoException();
                        parcel2.writeInt(iCreateDisplay);
                        break;
                    case 4:
                        releaseDisplay(parcel.readInt());
                        parcel2.writeNoException();
                        break;
                    case 5:
                        byte[] bArrCaptureDisplay = captureDisplay(parcel.readInt(), parcel.readInt());
                        parcel2.writeNoException();
                        parcel2.writeByteArray(bArrCaptureDisplay);
                        break;
                    case 6:
                        int iFrameBlank = frameBlank(parcel.readInt());
                        parcel2.writeNoException();
                        parcel2.writeInt(iFrameBlank);
                        break;
                    case 7:
                        String strTasks = tasks();
                        parcel2.writeNoException();
                        parcel2.writeString(strTasks);
                        break;
                    case 8:
                        boolean zMoveTask = moveTask(parcel.readInt(), parcel.readInt());
                        parcel2.writeNoException();
                        parcel2.writeInt(zMoveTask ? 1 : 0);
                        break;
                    case 9:
                        boolean audioMuted = setAudioMuted(parcel.readString(), parcel.readInt() != 0);
                        parcel2.writeNoException();
                        parcel2.writeInt(audioMuted ? 1 : 0);
                        break;
                    case 10:
                        restoreAudio();
                        parcel2.writeNoException();
                        break;
                    case 11:
                        String strLastDisplayError = lastDisplayError();
                        parcel2.writeNoException();
                        parcel2.writeString(strLastDisplayError);
                        break;
                    case 12:
                        String strFsStat = fsStat(parcel.readString());
                        parcel2.writeNoException();
                        parcel2.writeString(strFsStat);
                        break;
                    case 13:
                        String strFsList = fsList(parcel.readString(), parcel.readInt() != 0, parcel.readInt());
                        parcel2.writeNoException();
                        parcel2.writeString(strFsList);
                        break;
                    case 14:
                        ParcelFileDescriptor parcelFileDescriptorFsOpen = fsOpen(parcel.readString(), parcel.readString());
                        parcel2.writeNoException();
                        _Parcel.writeTypedObject(parcel2, parcelFileDescriptorFsOpen, 1);
                        break;
                    case 15:
                        String strFsMkdirs = fsMkdirs(parcel.readString());
                        parcel2.writeNoException();
                        parcel2.writeString(strFsMkdirs);
                        break;
                    case 16:
                        String strFsMove = fsMove(parcel.readString(), parcel.readString(), parcel.readInt() != 0);
                        parcel2.writeNoException();
                        parcel2.writeString(strFsMove);
                        break;
                    case 17:
                        String strFsDelete = fsDelete(parcel.readString());
                        parcel2.writeNoException();
                        parcel2.writeString(strFsDelete);
                        break;
                    case 18:
                        String strFsSearch = fsSearch(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readInt() != 0, parcel.readInt(), parcel.readString());
                        parcel2.writeNoException();
                        parcel2.writeString(strFsSearch);
                        break;
                    case 19:
                        boolean zWakeDisplay = wakeDisplay(parcel.readInt());
                        parcel2.writeNoException();
                        parcel2.writeInt(zWakeDisplay ? 1 : 0);
                        break;
                    case 20:
                        int iDisplayFlags = displayFlags(parcel.readInt());
                        parcel2.writeNoException();
                        parcel2.writeInt(iDisplayFlags);
                        break;
                    case 21:
                        String strTrustedDisplayDenied = trustedDisplayDenied();
                        parcel2.writeNoException();
                        parcel2.writeString(strTrustedDisplayDenied);
                        break;
                    default:
                        return super.onTransact(i, parcel, parcel2, i2);
                }
            }
            return true;
        }

        private static class Proxy implements IShellService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IShellService.DESCRIPTOR;
            }

            @Override // io.github.jackwener.dsh.core.platform.IShellService
            public void destroy() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IShellService.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_destroy, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // io.github.jackwener.dsh.core.platform.IShellService
            public String exec(String cmd, long timeoutMs) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IShellService.DESCRIPTOR);
                    parcelObtain.writeString(cmd);
                    parcelObtain.writeLong(timeoutMs);
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readString();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // io.github.jackwener.dsh.core.platform.IShellService
            public int createDisplay(int width, int height, int dpi) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IShellService.DESCRIPTOR);
                    parcelObtain.writeInt(width);
                    parcelObtain.writeInt(height);
                    parcelObtain.writeInt(dpi);
                    this.mRemote.transact(3, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // io.github.jackwener.dsh.core.platform.IShellService
            public void releaseDisplay(int displayId) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IShellService.DESCRIPTOR);
                    parcelObtain.writeInt(displayId);
                    this.mRemote.transact(4, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // io.github.jackwener.dsh.core.platform.IShellService
            public byte[] captureDisplay(int displayId, int maxWidth) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IShellService.DESCRIPTOR);
                    parcelObtain.writeInt(displayId);
                    parcelObtain.writeInt(maxWidth);
                    this.mRemote.transact(5, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.createByteArray();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // io.github.jackwener.dsh.core.platform.IShellService
            public int frameBlank(int displayId) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IShellService.DESCRIPTOR);
                    parcelObtain.writeInt(displayId);
                    this.mRemote.transact(6, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // io.github.jackwener.dsh.core.platform.IShellService
            public String tasks() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IShellService.DESCRIPTOR);
                    this.mRemote.transact(7, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readString();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // io.github.jackwener.dsh.core.platform.IShellService
            public boolean moveTask(int taskId, int displayId) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IShellService.DESCRIPTOR);
                    parcelObtain.writeInt(taskId);
                    parcelObtain.writeInt(displayId);
                    this.mRemote.transact(8, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // io.github.jackwener.dsh.core.platform.IShellService
            public boolean setAudioMuted(String str, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IShellService.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(9, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // io.github.jackwener.dsh.core.platform.IShellService
            public void restoreAudio() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IShellService.DESCRIPTOR);
                    this.mRemote.transact(10, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // io.github.jackwener.dsh.core.platform.IShellService
            public String lastDisplayError() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IShellService.DESCRIPTOR);
                    this.mRemote.transact(11, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readString();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // io.github.jackwener.dsh.core.platform.IShellService
            public boolean wakeDisplay(int displayId) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IShellService.DESCRIPTOR);
                    parcelObtain.writeInt(displayId);
                    this.mRemote.transact(19, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // io.github.jackwener.dsh.core.platform.IShellService
            public int displayFlags(int displayId) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IShellService.DESCRIPTOR);
                    parcelObtain.writeInt(displayId);
                    this.mRemote.transact(20, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // io.github.jackwener.dsh.core.platform.IShellService
            public String trustedDisplayDenied() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IShellService.DESCRIPTOR);
                    this.mRemote.transact(21, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readString();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // io.github.jackwener.dsh.core.platform.IShellService
            public String fsStat(String path) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IShellService.DESCRIPTOR);
                    parcelObtain.writeString(path);
                    this.mRemote.transact(12, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readString();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // io.github.jackwener.dsh.core.platform.IShellService
            public String fsList(String str, boolean z, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IShellService.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeInt(z ? 1 : 0);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(13, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readString();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // io.github.jackwener.dsh.core.platform.IShellService
            public ParcelFileDescriptor fsOpen(String path, String mode) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IShellService.DESCRIPTOR);
                    parcelObtain.writeString(path);
                    parcelObtain.writeString(mode);
                    this.mRemote.transact(14, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return (ParcelFileDescriptor) _Parcel.readTypedObject(parcelObtain2, ParcelFileDescriptor.CREATOR);
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // io.github.jackwener.dsh.core.platform.IShellService
            public String fsMkdirs(String path) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IShellService.DESCRIPTOR);
                    parcelObtain.writeString(path);
                    this.mRemote.transact(15, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readString();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // io.github.jackwener.dsh.core.platform.IShellService
            public String fsMove(String str, String str2, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IShellService.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeString(str2);
                    parcelObtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(16, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readString();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // io.github.jackwener.dsh.core.platform.IShellService
            public String fsDelete(String path) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IShellService.DESCRIPTOR);
                    parcelObtain.writeString(path);
                    this.mRemote.transact(17, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readString();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // io.github.jackwener.dsh.core.platform.IShellService
            public String fsSearch(String str, String str2, String str3, boolean z, int i, String str4) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IShellService.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeString(str2);
                    parcelObtain.writeString(str3);
                    parcelObtain.writeInt(z ? 1 : 0);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeString(str4);
                    this.mRemote.transact(18, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readString();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }
    }

    public static class _Parcel {
        /* JADX INFO: Access modifiers changed from: private */
        public static <T> T readTypedObject(Parcel parcel, Parcelable.Creator<T> c) {
            if (parcel.readInt() != 0) {
                return c.createFromParcel(parcel);
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static <T extends Parcelable> void writeTypedObject(Parcel parcel, T value, int parcelableFlags) {
            if (value != null) {
                parcel.writeInt(1);
                value.writeToParcel(parcel, parcelableFlags);
            } else {
                parcel.writeInt(0);
            }
        }
    }
}

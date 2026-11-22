package io.github.jackwener.dsh.core.engine;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

/* JADX INFO: loaded from: /data/user/0/io.github.jackwener.dsh/files/home/apk_x/classes3.dex */
public interface IEngineService extends IInterface {
    public static final String DESCRIPTOR = "io.github.jackwener.dsh.core.engine.IEngineService";

    public static class Default implements IEngineService {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // io.github.jackwener.dsh.core.engine.IEngineService
        public boolean isRunning() throws RemoteException {
            return false;
        }

        @Override // io.github.jackwener.dsh.core.engine.IEngineService
        public Bundle start(Bundle env) throws RemoteException {
            return null;
        }
    }

    boolean isRunning() throws RemoteException;

    Bundle start(Bundle env) throws RemoteException;

    public static abstract class Stub extends Binder implements IEngineService {
        static final int TRANSACTION_isRunning = 2;
        static final int TRANSACTION_start = 1;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IEngineService.DESCRIPTOR);
        }

        public static IEngineService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = obj.queryLocalInterface(IEngineService.DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IEngineService)) {
                return (IEngineService) iInterfaceQueryLocalInterface;
            }
            return new Proxy(obj);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IEngineService.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(IEngineService.DESCRIPTOR);
                return true;
            }
            if (i == 1) {
                Bundle bundleStart = start((Bundle) _Parcel.readTypedObject(parcel, Bundle.CREATOR));
                parcel2.writeNoException();
                _Parcel.writeTypedObject(parcel2, bundleStart, 1);
            } else if (i == 2) {
                boolean zIsRunning = isRunning();
                parcel2.writeNoException();
                parcel2.writeInt(zIsRunning ? 1 : 0);
            } else {
                return super.onTransact(i, parcel, parcel2, i2);
            }
            return true;
        }

        private static class Proxy implements IEngineService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IEngineService.DESCRIPTOR;
            }

            @Override // io.github.jackwener.dsh.core.engine.IEngineService
            public Bundle start(Bundle env) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IEngineService.DESCRIPTOR);
                    _Parcel.writeTypedObject(parcelObtain, env, 0);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return (Bundle) _Parcel.readTypedObject(parcelObtain2, Bundle.CREATOR);
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // io.github.jackwener.dsh.core.engine.IEngineService
            public boolean isRunning() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IEngineService.DESCRIPTOR);
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return parcelObtain2.readInt() != 0;
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

/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/dieter/eclipse/workspace/Ariadne/src/org/ruleant/ariadne/ILocationServiceCallback.aidl
 */
package org.ruleant.ariadne;
/**
 * Callback interface used by LocationService to send
 * synchronous notifications back to its clients.  Note that this is a
 * one-way interface so the server does not block waiting for the client.
 */
public interface ILocationServiceCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements org.ruleant.ariadne.ILocationServiceCallback
{
private static final java.lang.String DESCRIPTOR = "org.ruleant.ariadne.ILocationServiceCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an org.ruleant.ariadne.ILocationServiceCallback interface,
 * generating a proxy if needed.
 */
public static org.ruleant.ariadne.ILocationServiceCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof org.ruleant.ariadne.ILocationServiceCallback))) {
return ((org.ruleant.ariadne.ILocationServiceCallback)iin);
}
return new org.ruleant.ariadne.ILocationServiceCallback.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_locationUpdated:
{
data.enforceInterface(DESCRIPTOR);
this.locationUpdated();
return true;
}
case TRANSACTION_providerUpdated:
{
data.enforceInterface(DESCRIPTOR);
this.providerUpdated();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements org.ruleant.ariadne.ILocationServiceCallback
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
/**
     * Called when the service has an updated location.
     */
@Override public void locationUpdated() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_locationUpdated, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
/**
     * Called when the service has an updated provider.
     */
@Override public void providerUpdated() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_providerUpdated, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
}
static final int TRANSACTION_locationUpdated = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_providerUpdated = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
/**
     * Called when the service has an updated location.
     */
public void locationUpdated() throws android.os.RemoteException;
/**
     * Called when the service has an updated provider.
     */
public void providerUpdated() throws android.os.RemoteException;
}

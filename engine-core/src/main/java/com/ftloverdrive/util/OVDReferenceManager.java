package com.ftloverdrive.util;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectIntMap;


/**
 * Associates objects with reference ids, to be looked up later.
 */
public class OVDReferenceManager {

	private IntMap<Object> idToObjMap = new IntMap<Object>();
	private ObjectIntMap<Object> objToIdMap = new ObjectIntMap<Object>();


	public OVDReferenceManager() {
	}


	/**
	 * Adds an object to be managed with the given id.
	 *
	 * @return  the assigned id
	 */
	public int addObject( Object o, int id ) {
		forget( o );
		forget( id );
		objToIdMap.put( o, id );
		idToObjMap.put( id, o );

		return id;
	}


	/**
	 * Returns the id for an object, or -1.
	 */
	public int getId( Object o ) {
		return objToIdMap.get( o, -1 );
	}


	/**
	 * Returns the object for an id, or null.
	 */
	public Object getObject( int id ) {
		return idToObjMap.get( id );
	}

	/**
	 * Returns the object for an id, or null.
	 */
	public <T> T getObject( int id, Class<T> objClass ) {
		Object result = idToObjMap.get( id );
		if ( result == null ) return null;
		return objClass.cast( result );
	}


	/**
	 * Stops managing an object, so the garbage collector can free it.
	 */
	public void forget( Object o ) {
		int id = objToIdMap.remove( o, -1 );
		if ( id != -1 )
			idToObjMap.remove( id );
	}

	public void forget( int id ) {
		Object o = idToObjMap.remove( id );
		if ( o != null )
			objToIdMap.remove( o, -1 );
	}
}

package net.sf.opengroove.client.workspace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * this list MUST NOT be used as is, but MUST be wrapped by
 * Collections.synchronizedList()
 * 
 * this is a list with set funtionality, IE it does not support duplicates. it
 * must be wrapped as a synchronized set, because all of the modifying methods
 * of ArrayList are overridden, called (by super.XXX()), and then a method is
 * called that scans the list for duplicates and removes them.
 * 
 * @author Alexander Boyd
 * 
 */
public class SetList<E> extends ArrayList<E>
{
	private void checkForDuplicates()
	{
		boolean modified = false;
		do
		{
			modified = false;
			for (E s : new ArrayList<E>(this))
			{
				if (super.indexOf(s) != super.lastIndexOf(s))
				{
					super.remove(s);// important to call super here (instead of
					// this), otherwise
					// you will get a StackOverflowError because
					// this.remove calls checkForDuplicates
					modified = true;
				}

			}
		} while (modified);
	}

	@Override
	public void add(int index, E element)
	{
		// TODO Auto-generated method stub
		super.add(index, element);
		checkForDuplicates();
	}

	@Override
	public boolean add(E e)
	{
		// TODO Auto-generated method stub
		boolean o = super.add(e);
		checkForDuplicates();
		return o;
	}

	@Override
	public boolean addAll(Collection<? extends E> c)
	{
		// TODO Auto-generated method stub
		boolean o = super.addAll(c);
		checkForDuplicates();
		return o;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c)
	{
		// TODO Auto-generated method stub
		boolean o = super.addAll(index, c);
		checkForDuplicates();
		return o;
	}

	@Override
	public void clear()
	{
		// TODO Auto-generated method stub
		super.clear();
		checkForDuplicates();
	}

	@Override
	public void ensureCapacity(int minCapacity)
	{
		// TODO Auto-generated method stub
		super.ensureCapacity(minCapacity);
		checkForDuplicates();
	}

	@Override
	public E remove(int index)
	{
		// TODO Auto-generated method stub
		E o = super.remove(index);
		checkForDuplicates();
		return o;
	}

	@Override
	public boolean remove(Object o)
	{
		// TODO Auto-generated method stub
		boolean o2 = super.remove(o);
		checkForDuplicates();
		return o2;
	}

	@Override
	public E set(int index, E element)
	{
		// TODO Auto-generated method stub
		E o = super.set(index, element);
		checkForDuplicates();
		return o;
	}

	@Override
	public void trimToSize()
	{
		// TODO Auto-generated method stub
		super.trimToSize();
		checkForDuplicates();
	}

	@Override
	public boolean removeAll(Collection c)
	{
		// TODO Auto-generated method stub
		boolean o = super.removeAll(c);
		checkForDuplicates();
		return o;
	}

	@Override
	public boolean retainAll(Collection c)
	{
		// TODO Auto-generated method stub
		boolean o = super.retainAll(c);
		checkForDuplicates();
		return o;
	}

	public SetList()
	{
		// TODO Auto-generated constructor stub
	}

	public SetList(int initialCapacity)
	{
		super(initialCapacity);
		// TODO Auto-generated constructor stub
	}

	public SetList(Collection<? extends E> c)
	{
		super(c);
		// TODO Auto-generated constructor stub
	}
	/**
	 * returns Collections.synchronizedList(new SetList());. this is a convienence method that creates a SetList wrapped in a synchronized list from Collections.
	 * it is reccomended to use this method to create a SetList, instead of the SetList constructors.
	 * @return
	 */
	public static <T> List<T> cs(Class<T> c)
	{
		return Collections.synchronizedList(new SetList<T>());
	}

}

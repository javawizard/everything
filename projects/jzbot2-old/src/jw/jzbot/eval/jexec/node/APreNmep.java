/* This file was generated by SableCC (http://www.sablecc.org/). */

package jw.jzbot.eval.jexec.node;

import jw.jzbot.eval.jexec.analysis.*;

@SuppressWarnings("nls")
public final class APreNmep extends PNmep
{
    private TName _name_;
    private PTerm _second_;

    public APreNmep()
    {
        // Constructor
    }

    public APreNmep(
        @SuppressWarnings("hiding") TName _name_,
        @SuppressWarnings("hiding") PTerm _second_)
    {
        // Constructor
        setName(_name_);

        setSecond(_second_);

    }

    @Override
    public Object clone()
    {
        return new APreNmep(
            cloneNode(this._name_),
            cloneNode(this._second_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAPreNmep(this);
    }

    public TName getName()
    {
        return this._name_;
    }

    public void setName(TName node)
    {
        if(this._name_ != null)
        {
            this._name_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._name_ = node;
    }

    public PTerm getSecond()
    {
        return this._second_;
    }

    public void setSecond(PTerm node)
    {
        if(this._second_ != null)
        {
            this._second_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._second_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._name_)
            + toString(this._second_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._name_ == child)
        {
            this._name_ = null;
            return;
        }

        if(this._second_ == child)
        {
            this._second_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._name_ == oldChild)
        {
            setName((TName) newChild);
            return;
        }

        if(this._second_ == oldChild)
        {
            setSecond((PTerm) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}

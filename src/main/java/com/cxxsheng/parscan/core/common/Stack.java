package com.cxxsheng.parscan.core.common;

public final class Stack {

  // Data
  //

  /** Stack depth. */
  private int fDepth;

  /** Stack data. */
  private int[] fData;

  //
  // Public methods
  //

  /** Returns the size of the stack. */
  public int size() {
    return fDepth;
  }

  /** Pushes a value onto the stack. */
  public void push(int value) {
    ensureCapacity(fDepth + 1);
    fData[fDepth++] = value;
  }

  /** Peeks at the top of the stack. */
  public int peek() {
    return fData[fDepth - 1];
  }

  /** Returns the element at the specified depth in the stack. */
  public int elementAt(int depth) {
    return fData[depth];
  }

  /** Pops a value off of the stack. */
  public int pop() {
    return fData[--fDepth];
  }

  /** Clears the stack. */
  public void clear() {
    fDepth = 0;
  }

  // debugging

  @Override

  /** tiString the stack. */
  public String toString() {
    StringBuilder sb = new StringBuilder();
    int a[] = fData;
    if (a == null)
      return "null";
    int iMax = fDepth - 1;
    if (iMax == -1)
      return "[]";

    StringBuilder b = new StringBuilder();
    b.append('[');
    for (int i = 0; ; i++) {
      b.append(a[i]);
      if (i == iMax)
        return b.append(']').toString();
      b.append(", ");
    }
  }


  //
  // Private methods
  //

  /** Ensures capacity. */
  private void ensureCapacity(int size) {
    if (fData == null) {
      fData = new int[32];
    }
    else if (fData.length <= size) {
      int[] newdata = new int[fData.length * 2];
      System.arraycopy(fData, 0, newdata, 0, fData.length);
      fData = newdata;
    }
  }

  public void set(int index, int value) {
    if (index >= fDepth)
      throw new IndexOutOfBoundsException();
    fData[index] = value;
  }

  public int get(int index){
    if (index >= fDepth)
      throw new IndexOutOfBoundsException();
    return fData[index];
  }

  public int[] toIntArray(){
    if (fData == null)
      return new int[0];
    int[] newData = new int[fDepth];
    System.arraycopy(fData, 0, newData ,0, fDepth);
    return newData;
  }

   public boolean empty(){
    return fDepth == 0;
   }

} // class IntStack
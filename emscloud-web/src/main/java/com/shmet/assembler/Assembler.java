package com.shmet.assembler;

/**
 * 实现该接口，将输入对象重新组装转化为输出对象
 */
public interface Assembler {
  Object assemble(Object inputData, Object outputMeta);
}

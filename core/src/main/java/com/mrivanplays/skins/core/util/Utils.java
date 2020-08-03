package com.mrivanplays.skins.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class Utils {

  public static <O, N> Collection<N> map(Collection<O> list, Function<O, N> mapper) {
    List<N> ret = new ArrayList<>();
    for (O o : list) {
      ret.add(mapper.apply(o));
    }
    return ret;
  }
}

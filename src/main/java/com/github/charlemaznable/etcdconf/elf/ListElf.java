package com.github.charlemaznable.etcdconf.elf;

import lombok.NoArgsConstructor;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ListElf {

    public static <T> List<T> addItemToList(List<T> list, T item) {
        if (isNull(list)) return newArrayList(item);
        list.add(item);
        return list;
    }
}

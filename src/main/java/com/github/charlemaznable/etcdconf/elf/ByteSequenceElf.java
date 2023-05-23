package com.github.charlemaznable.etcdconf.elf;

import io.etcd.jetcd.ByteSequence;
import lombok.NoArgsConstructor;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ByteSequenceElf {

    public static ByteSequence toByteSequence(String str) {
        return ByteSequence.from(str.getBytes(UTF_8));
    }

    public static String fromByteSequence(ByteSequence byteSequence) {
        return requireNonNull(byteSequence).toString(UTF_8);
    }
}

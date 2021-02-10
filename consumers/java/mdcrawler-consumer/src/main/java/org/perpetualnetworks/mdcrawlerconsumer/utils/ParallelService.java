package org.perpetualnetworks.mdcrawlerconsumer.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Slf4j
public class ParallelService {

    private final ForkJoinPool forkJoinPool;

    public ParallelService(int threadPoolSize) {
        forkJoinPool = new ForkJoinPool(threadPoolSize);
    }

    @SneakyThrows
    public <InputT, ResultT> ResultT executeAndReturnParallelAndEventAware(Function<Stream<InputT>, ResultT> streamFunction, Collection<InputT> inputList) {
        return forkJoinPool
                .submit(() -> executeAndReturn(() ->
                        streamFunction.apply(inputList.parallelStream())))
                .get();
    }

    @SneakyThrows
    public static <OutputT> OutputT executeAndReturn(Supplier<OutputT> function) {
        try {
            return function.get();
        } finally {
            //TODO: add event handle
        }
    }
}
/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.redis.connection.lettuce;

import java.nio.ByteBuffer;

import org.reactivestreams.Publisher;
import org.springframework.data.redis.connection.ReactiveHyperLogLogCommands;
import org.springframework.data.redis.connection.ReactiveRedisConnection.BooleanResponse;
import org.springframework.data.redis.connection.ReactiveRedisConnection.NumericResponse;
import org.springframework.util.Assert;

import reactor.core.publisher.Flux;

/**
 * @author Christoph Strobl
 * @since 2.0
 */
public class LettuceReactiveHyperLogLogCommands implements ReactiveHyperLogLogCommands {

	private final LettuceReactiveRedisConnection connection;

	/**
	 * Create new {@link LettuceReactiveHyperLogLogCommands}.
	 *
	 * @param connection must not be {@literal null}.
	 */
	public LettuceReactiveHyperLogLogCommands(LettuceReactiveRedisConnection connection) {

		Assert.notNull(connection, "Connection must not be null!");
		this.connection = connection;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.redis.connection.ReactiveHyperLogLogCommands#pfAdd(org.reactivestreams.Publisher)
	 */
	@Override
	public Flux<NumericResponse<PfAddCommand, Long>> pfAdd(Publisher<PfAddCommand> commands) {

		return connection.execute(cmd -> {

			return Flux.from(commands).flatMap(command -> {
				return LettuceReactiveRedisConnection.<Long> monoConverter()
						.convert(cmd.pfadd(command.getKey().array(),
								command.getValues().stream().map(ByteBuffer::array).toArray(size -> new byte[size][])))
						.map(value -> new NumericResponse<>(command, value));
			});
		});
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.redis.connection.ReactiveHyperLogLogCommands#pfCount(org.reactivestreams.Publisher)
	 */
	@Override
	public Flux<NumericResponse<PfCountCommand, Long>> pfCount(Publisher<PfCountCommand> commands) {

		return connection.execute(cmd -> {

			return Flux.from(commands).flatMap(command -> {
				return LettuceReactiveRedisConnection.<Long> monoConverter()
						.convert(cmd.pfcount(command.getKeys().stream().map(ByteBuffer::array).toArray(size -> new byte[size][])))
						.map(value -> new NumericResponse<>(command, value));
			});
		});
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.redis.connection.ReactiveHyperLogLogCommands#pfMerge(org.reactivestreams.Publisher)
	 */
	@Override
	public Flux<BooleanResponse<PfMergeCommand>> pfMerge(Publisher<PfMergeCommand> commands) {

		return connection.execute(cmd -> {

			return Flux.from(commands).flatMap(command -> {
				return LettuceReactiveRedisConnection.<Boolean> monoConverter()
						.convert(cmd
								.pfmerge(command.getKey().array(),
										command.getSourceKeys().stream().map(ByteBuffer::array).toArray(size -> new byte[size][]))
								.map(LettuceConverters::stringToBoolean))
						.map(value -> new BooleanResponse<>(command, value));
			});
		});
	}

}
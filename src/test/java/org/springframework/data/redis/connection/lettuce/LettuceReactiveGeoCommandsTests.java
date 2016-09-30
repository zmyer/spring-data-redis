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

import static org.hamcrest.core.Is.*;
import static org.hamcrest.number.IsCloseTo.*;
import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.junit.Test;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;

/**
 * @author Christoph Strobl
 */
public class LettuceReactiveGeoCommandsTests extends LettuceReactiveCommandsTestsBase {

	private static final String ARIGENTO_MEMBER_NAME = "arigento";
	private static final String CATANIA_MEMBER_NAME = "catania";
	private static final String PALERMO_MEMBER_NAME = "palermo";

	private static final Point POINT_ARIGENTO = new Point(13.583333, 37.316667);
	private static final Point POINT_CATANIA = new Point(15.087269, 37.502669);
	private static final Point POINT_PALERMO = new Point(13.361389, 38.115556);

	private static final GeoLocation<ByteBuffer> ARIGENTO = new GeoLocation<>(
			ByteBuffer.wrap(ARIGENTO_MEMBER_NAME.getBytes(Charset.forName("UTF-8"))), POINT_ARIGENTO);
	private static final GeoLocation<ByteBuffer> CATANIA = new GeoLocation<>(
			ByteBuffer.wrap(CATANIA_MEMBER_NAME.getBytes(Charset.forName("UTF-8"))), POINT_CATANIA);
	private static final GeoLocation<ByteBuffer> PALERMO = new GeoLocation<>(
			ByteBuffer.wrap(PALERMO_MEMBER_NAME.getBytes(Charset.forName("UTF-8"))), POINT_PALERMO);

	/**
	 * @see DATAREDIS-525
	 */
	@Test
	public void geoAddShouldAddSingleGeoLocationCorrectly() {
		assertThat(connection.geoCommands().geoAdd(KEY_1_BBUFFER, ARIGENTO).block(), is(1L));
	}

	/**
	 * @see DATAREDIS-525
	 */
	@Test
	public void geoAddShouldAddMultipleGeoLocationsCorrectly() {
		assertThat(connection.geoCommands().geoAdd(KEY_1_BBUFFER, Arrays.asList(ARIGENTO, CATANIA, PALERMO)).block(),
				is(3L));
	}

	/**
	 * @see DATAREDIS-525
	 */
	@Test
	public void geoDistShouldReturnDistanceInMetersByDefault() {

		nativeCommands.geoadd(KEY_1, PALERMO.getPoint().getX(), PALERMO.getPoint().getY(), PALERMO_MEMBER_NAME);
		nativeCommands.geoadd(KEY_1, CATANIA.getPoint().getX(), CATANIA.getPoint().getY(), CATANIA_MEMBER_NAME);

		assertThat(connection.geoCommands().geoDist(KEY_1_BBUFFER, PALERMO.getName(), CATANIA.getName()).block().getValue(),
				is(closeTo(166274.15156960033D, 0.005)));
	}

	/**
	 * @see DATAREDIS-525
	 */
	@Test
	public void geoDistShouldReturnDistanceInDesiredMetric() {

		nativeCommands.geoadd(KEY_1, PALERMO.getPoint().getX(), PALERMO.getPoint().getY(), PALERMO_MEMBER_NAME);
		nativeCommands.geoadd(KEY_1, CATANIA.getPoint().getX(), CATANIA.getPoint().getY(), CATANIA_MEMBER_NAME);

		assertThat(connection.geoCommands().geoDist(KEY_1_BBUFFER, PALERMO.getName(), CATANIA.getName(), Metrics.KILOMETERS)
				.block().getValue(), is(closeTo(166.27415156960033D, 0.005)));
	}

}

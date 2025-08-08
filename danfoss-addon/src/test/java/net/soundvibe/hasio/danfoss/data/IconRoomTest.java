package net.soundvibe.hasio.danfoss.data;

import net.soundvibe.hasio.Json;
import net.soundvibe.hasio.model.Command;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class IconRoomTest {

    private static final Logger logger = LoggerFactory.getLogger(IconRoomTest.class);

    @Test
    void test_state_json() {
        var sut = new IconRoom("Living Room", 1, 22.3, 23.0, 21.0, 19.0,
                 30.0, 15.0, (short) 99, HeatingState.OFF, RoomMode.HOME);
        var state = sut.toState();

        logger.info(Json.toJsonString(state));
    }

    @Test
    void test_temperature_sensor_entity() {
        var sut = new IconRoom("Living Room", 1, 22.3, 23.0, 21.0, 19.0,
                 30.0, 15.0, (short) 99, HeatingState.OFF, RoomMode.HOME);
        var iconMaster = new IconMaster("Test House", 17.0, 21.0, "1.0", "21.0", "test-serial",
                1, 1, java.time.Instant.parse("2023-12-03T10:15:30.00Z"));
        var temperatureSensor = sut.toMQTTTemperatureSensorEntity("danfoss_icon_thermostat_room_1", "danfoss/icon/%d/state", iconMaster);

        logger.info("Temperature sensor entity: {}", Json.toJsonString(temperatureSensor));
        
        assertEquals("danfoss_icon_thermostat_room_1_temperature", temperatureSensor.unique_id());
        assertEquals("Temperature", temperatureSensor.name());
        assertEquals("sensor", temperatureSensor.componentType());
        assertEquals("{{ value_json.state }}", temperatureSensor.value_template());
        assertEquals("°C", temperatureSensor.unit_of_measurement());
        assertEquals("temperature", temperatureSensor.device_class());
        assertEquals("measurement", temperatureSensor.state_class());
    }

    @Test
    void should_unmarshal_command() {
        var commandJson = """
                {"command": "setHomeTemperature","value":"23.5","roomNumber":"0"}""";

        var actual = Json.fromString(commandJson, Command.class);
        assertEquals(23.5, actual.value());
        assertEquals(0, actual.roomNumber());
        assertEquals("setHomeTemperature", actual.command());
    }
}
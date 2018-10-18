// AUTO-GENERATED CODE: DO NOT EDIT!!!

package com.msppg;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Parser {

    private int state;
    private byte message_direction;
    private byte message_id;
    private byte message_length_expected;
    private byte message_length_received;
    private ByteArrayOutputStream message_buffer;
    private byte message_checksum;

    public Parser() {

        this.state = 0;
        this.message_buffer = new ByteArrayOutputStream();
    }

    private static ByteBuffer newByteBuffer(int capacity) {
        ByteBuffer bb = ByteBuffer.allocate(capacity);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb;
    }

   private static byte CRC8(byte [] data, int beg, int end) {

        int crc = 0x00;

        for (int k=beg; k<end; ++k) {

            int extract = (int)data[k] & 0xFF;

            crc ^= extract;
        }

        return (byte)crc;
    }

    public void parse(byte b) {

        switch (this.state) {

            case 0:               // sync char 1
                if (b == 36) { // $
                    this.state++;
                }
                break;        

            case 1:               // sync char 2
                if (b == 77) { // M
                    this.state++;
                }
                else {            // restart and try again
                    this.state = 0;
                }
                break;

            case 2:               // direction (should be >)
                if (b == 62) { // >
                    this.message_direction = 1;
                }
                else {            // <
                    this.message_direction = 0;
                }
                this.state++;
                break;

            case 3:
                this.message_length_expected = b;
                this.message_checksum = b;
                // setup arraybuffer
                this.message_length_received = 0;
                this.state++;
                break;

            case 4:
                this.message_id = b;
                this.message_checksum ^= b;
                this.message_buffer.reset();
                if (this.message_length_expected > 0) {
                    // process payload
                    this.state++;
                }
                else {
                    // no payload
                    this.state += 2;
                }
                break;

            case 5: // payload
                this.message_buffer.write(b);
                this.message_checksum ^= b;
                this.message_length_received++;
                if (this.message_length_received >= this.message_length_expected) {
                    this.state++;
                }
                break;

            case 6:
                this.state = 0;
                if (this.message_checksum == b) {

                    ByteBuffer bb = newByteBuffer(this.message_length_received);
                    bb.put(this.message_buffer.toByteArray(), 0, this.message_length_received);

                    switch (this.message_id) {
                        case (byte)105:
                            if (this.RC_handler != null) {
                                this.RC_handler.handle_RC(
                                bb.getShort(0),
                                bb.getShort(2),
                                bb.getShort(4),
                                bb.getShort(6),
                                bb.getShort(8),
                                bb.getShort(10),
                                bb.getShort(12),
                                bb.getShort(14));
                            }
                            break;

                        case (byte)102:
                            if (this.IMU_handler != null) {
                                this.IMU_handler.handle_IMU(
                                bb.getShort(0),
                                bb.getShort(2),
                                bb.getShort(4),
                                bb.getShort(6),
                                bb.getShort(8),
                                bb.getShort(10),
                                bb.getShort(12),
                                bb.getShort(14),
                                bb.getShort(16));
                            }
                            break;

                        case (byte)127:
                            if (this.SONARS_handler != null) {
                                this.SONARS_handler.handle_SONARS(
                                bb.getShort(0),
                                bb.getShort(2),
                                bb.getShort(4),
                                bb.getShort(6));
                            }
                            break;

                        case (byte)109:
                            if (this.ALTITUDE_handler != null) {
                                this.ALTITUDE_handler.handle_ALTITUDE(
                                bb.getInt(0),
                                bb.getShort(4));
                            }
                            break;

                        case (byte)108:
                            if (this.ATTITUDE_handler != null) {
                                this.ATTITUDE_handler.handle_ATTITUDE(
                                bb.getShort(0),
                                bb.getShort(2),
                                bb.getShort(4));
                            }
                            break;

                        case (byte)106:
                            if (this.GPS_handler != null) {
                                this.GPS_handler.handle_GPS(
                                bb.get(0),
                                bb.get(1),
                                bb.getInt(2),
                                bb.getInt(6),
                                bb.getShort(10),
                                bb.getShort(12),
                                bb.getShort(14));
                            }
                            break;


                    }
                }
        }
    }

    private RC_Handler RC_handler;

    public void set_RC_Handler(RC_Handler handler) {

        this.RC_handler = handler;
    }

    public byte [] serialize_RC_Request() {


        byte [] message = new byte[6];

        message[0] = 36;
        message[1] = 77;
        message[2] = 60;
        message[3] = 0;
        message[4] = (byte)105;
        message[5] = (byte)105;

        return message;
    }

    public byte [] serialize_RC(short c1, short c2, short c3, short c4, short c5, short c6, short c7, short c8) {

        ByteBuffer bb = newByteBuffer(16);

        bb.putShort(c1);
        bb.putShort(c2);
        bb.putShort(c3);
        bb.putShort(c4);
        bb.putShort(c5);
        bb.putShort(c6);
        bb.putShort(c7);
        bb.putShort(c8);

        byte [] message = new byte[22];
        message[0] = 36;
        message[1] = 77;
        message[2] = 62;
        message[3] = 16;
        message[4] = (byte)105;
        byte [] data = bb.array();
        int k;
        for (k=0; k<data.length; ++k) {
            message[k+5] = data[k];
        }

        message[21] = CRC8(message, 3, 20);

        return message;
    }

    private IMU_Handler IMU_handler;

    public void set_IMU_Handler(IMU_Handler handler) {

        this.IMU_handler = handler;
    }

    public byte [] serialize_IMU_Request() {


        byte [] message = new byte[6];

        message[0] = 36;
        message[1] = 77;
        message[2] = 60;
        message[3] = 0;
        message[4] = (byte)102;
        message[5] = (byte)102;

        return message;
    }

    public byte [] serialize_IMU(short accx, short accy, short accz, short gyrx, short gyry, short gyrz, short magx, short magy, short magz) {

        ByteBuffer bb = newByteBuffer(18);

        bb.putShort(accx);
        bb.putShort(accy);
        bb.putShort(accz);
        bb.putShort(gyrx);
        bb.putShort(gyry);
        bb.putShort(gyrz);
        bb.putShort(magx);
        bb.putShort(magy);
        bb.putShort(magz);

        byte [] message = new byte[24];
        message[0] = 36;
        message[1] = 77;
        message[2] = 62;
        message[3] = 18;
        message[4] = (byte)102;
        byte [] data = bb.array();
        int k;
        for (k=0; k<data.length; ++k) {
            message[k+5] = data[k];
        }

        message[23] = CRC8(message, 3, 22);

        return message;
    }

    private SONARS_Handler SONARS_handler;

    public void set_SONARS_Handler(SONARS_Handler handler) {

        this.SONARS_handler = handler;
    }

    public byte [] serialize_SONARS_Request() {


        byte [] message = new byte[6];

        message[0] = 36;
        message[1] = 77;
        message[2] = 60;
        message[3] = 0;
        message[4] = (byte)127;
        message[5] = (byte)127;

        return message;
    }

    public byte [] serialize_SONARS(short back, short front, short left, short right) {

        ByteBuffer bb = newByteBuffer(8);

        bb.putShort(back);
        bb.putShort(front);
        bb.putShort(left);
        bb.putShort(right);

        byte [] message = new byte[14];
        message[0] = 36;
        message[1] = 77;
        message[2] = 62;
        message[3] = 8;
        message[4] = (byte)127;
        byte [] data = bb.array();
        int k;
        for (k=0; k<data.length; ++k) {
            message[k+5] = data[k];
        }

        message[13] = CRC8(message, 3, 12);

        return message;
    }

    public byte [] serialize_SET_MOTOR(short m1, short m2, short m3, short m4) {

        ByteBuffer bb = newByteBuffer(8);

        bb.putShort(m1);
        bb.putShort(m2);
        bb.putShort(m3);
        bb.putShort(m4);

        byte [] message = new byte[14];
        message[0] = 36;
        message[1] = 77;
        message[2] = 60;
        message[3] = 8;
        message[4] = (byte)214;
        byte [] data = bb.array();
        int k;
        for (k=0; k<data.length; ++k) {
            message[k+5] = data[k];
        }

        message[13] = CRC8(message, 3, 12);

        return message;
    }

    public byte [] serialize_SET_RAW_RC(short c1, short c2, short c3, short c4, short c5, short c6, short c7, short c8) {

        ByteBuffer bb = newByteBuffer(16);

        bb.putShort(c1);
        bb.putShort(c2);
        bb.putShort(c3);
        bb.putShort(c4);
        bb.putShort(c5);
        bb.putShort(c6);
        bb.putShort(c7);
        bb.putShort(c8);

        byte [] message = new byte[22];
        message[0] = 36;
        message[1] = 77;
        message[2] = 60;
        message[3] = 16;
        message[4] = (byte)200;
        byte [] data = bb.array();
        int k;
        for (k=0; k<data.length; ++k) {
            message[k+5] = data[k];
        }

        message[21] = CRC8(message, 3, 20);

        return message;
    }

    public byte [] serialize_SET_HEAD(short head) {

        ByteBuffer bb = newByteBuffer(2);

        bb.putShort(head);

        byte [] message = new byte[8];
        message[0] = 36;
        message[1] = 77;
        message[2] = 60;
        message[3] = 2;
        message[4] = (byte)205;
        byte [] data = bb.array();
        int k;
        for (k=0; k<data.length; ++k) {
            message[k+5] = data[k];
        }

        message[7] = CRC8(message, 3, 6);

        return message;
    }

    private ALTITUDE_Handler ALTITUDE_handler;

    public void set_ALTITUDE_Handler(ALTITUDE_Handler handler) {

        this.ALTITUDE_handler = handler;
    }

    public byte [] serialize_ALTITUDE_Request() {


        byte [] message = new byte[6];

        message[0] = 36;
        message[1] = 77;
        message[2] = 60;
        message[3] = 0;
        message[4] = (byte)109;
        message[5] = (byte)109;

        return message;
    }

    public byte [] serialize_ALTITUDE(int altitude, short vario) {

        ByteBuffer bb = newByteBuffer(6);

        bb.putInt(altitude);
        bb.putShort(vario);

        byte [] message = new byte[12];
        message[0] = 36;
        message[1] = 77;
        message[2] = 62;
        message[3] = 6;
        message[4] = (byte)109;
        byte [] data = bb.array();
        int k;
        for (k=0; k<data.length; ++k) {
            message[k+5] = data[k];
        }

        message[11] = CRC8(message, 3, 10);

        return message;
    }

    private ATTITUDE_Handler ATTITUDE_handler;

    public void set_ATTITUDE_Handler(ATTITUDE_Handler handler) {

        this.ATTITUDE_handler = handler;
    }

    public byte [] serialize_ATTITUDE_Request() {


        byte [] message = new byte[6];

        message[0] = 36;
        message[1] = 77;
        message[2] = 60;
        message[3] = 0;
        message[4] = (byte)108;
        message[5] = (byte)108;

        return message;
    }

    public byte [] serialize_ATTITUDE(short roll, short pitch, short yaw) {

        ByteBuffer bb = newByteBuffer(6);

        bb.putShort(roll);
        bb.putShort(pitch);
        bb.putShort(yaw);

        byte [] message = new byte[12];
        message[0] = 36;
        message[1] = 77;
        message[2] = 62;
        message[3] = 6;
        message[4] = (byte)108;
        byte [] data = bb.array();
        int k;
        for (k=0; k<data.length; ++k) {
            message[k+5] = data[k];
        }

        message[11] = CRC8(message, 3, 10);

        return message;
    }

    private GPS_Handler GPS_handler;

    public void set_GPS_Handler(GPS_Handler handler) {

        this.GPS_handler = handler;
    }

    public byte [] serialize_GPS_Request() {


        byte [] message = new byte[6];

        message[0] = 36;
        message[1] = 77;
        message[2] = 60;
        message[3] = 0;
        message[4] = (byte)106;
        message[5] = (byte)106;

        return message;
    }

    public byte [] serialize_GPS(byte fix, byte numsat, int lat, int lon, short altitude, short speed, short course) {

        ByteBuffer bb = newByteBuffer(16);

        bb.put(fix);
        bb.put(numsat);
        bb.putInt(lat);
        bb.putInt(lon);
        bb.putShort(altitude);
        bb.putShort(speed);
        bb.putShort(course);

        byte [] message = new byte[22];
        message[0] = 36;
        message[1] = 77;
        message[2] = 62;
        message[3] = 16;
        message[4] = (byte)106;
        byte [] data = bb.array();
        int k;
        for (k=0; k<data.length; ++k) {
            message[k+5] = data[k];
        }

        message[21] = CRC8(message, 3, 20);

        return message;
    }

}
package com.github.imusk.im.study.protobuf.basic;

import com.github.imusk.im.study.proto.demo.AddressBookProto;
import com.github.imusk.im.study.proto.demo.AnyProto;
import com.github.imusk.im.study.proto.demo.MapProto;
import com.github.imusk.im.study.proto.demo.OneOfProto;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * @classname: Demo
 * @description: Demo
 * @data: 2020-07-17 00:07
 * @author: Musk
 */
public class Demo {

    public static void main(String args[]) {
        //ConstructAndSerializeToLocalFile();
        //DeserializeFromFile();

        //FromAndToByteArray();

        //AnyTest();

        OneOfTest();

        //MapTest();

        //FromAndToJson();

        System.out.println("done");
    }

    //construct a Person object, and then serialize it.
    private static void ConstructAndSerializeToLocalFile() {
        AddressBookProto.Person.Builder person = AddressBookProto.Person.newBuilder();

        person.setId(1);
        person.setName("Lilei");
        person.setEmail("fqyang@163.com");

        AddressBookProto.Person.PhoneNumber.Builder MobileNumber = AddressBookProto.Person.PhoneNumber.newBuilder().setNumber("4099134756");
        MobileNumber.setType(AddressBookProto.Person.PhoneType.MOBILE);
        person.addPhones(MobileNumber);

        AddressBookProto.Person.PhoneNumber.Builder HomeNumber = AddressBookProto.Person.PhoneNumber.newBuilder().setNumber("10086");
        HomeNumber.setType(AddressBookProto.Person.PhoneType.HOME);
        person.addPhones(HomeNumber);

        FileOutputStream output = null;
        try {
            output = new FileOutputStream("C:\\D_study\\Protobuf\\exercise\\demo1.txt");
            person.build().writeTo(output);
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void DeserializeFromFile() {
        String MessageFile = "C:\\IM_Study\\Protobuf\\exercise\\demo.txt";
        // Read the existing address book.
        try {
            AddressBookProto.Person person = AddressBookProto.Person.parseFrom(new FileInputStream(MessageFile));

            System.out.println("Person ID: " + person.getId());
            System.out.println("Name: " + person.getName());
            System.out.println("Email: " + person.getEmail());

            for (AddressBookProto.Person.PhoneNumber phoneNumber : person.getPhonesList()) {
                switch (phoneNumber.getTypeValue()) {
                    case AddressBookProto.Person.PhoneType.MOBILE_VALUE:
                        System.out.print("  Mobile phone #: ");
                        break;
                    case AddressBookProto.Person.PhoneType.HOME_VALUE:
                        System.out.print("  Home phone #: ");
                        break;
                    case AddressBookProto.Person.PhoneType.WORK_VALUE:
                        System.out.print("  Work phone #: ");
                        break;
                    default:
                        break;
                }
                System.out.println(phoneNumber.getNumber());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void FromAndToByteArray() {
        AddressBookProto.Person.Builder person = AddressBookProto.Person.newBuilder();

        person.setId(1);
        person.setName("Lilei");
        person.setEmail("fqyang@163.com");

        AddressBookProto.Person.PhoneNumber.Builder MobileNumber = AddressBookProto.Person.PhoneNumber.newBuilder().setNumber("4099134756");
        MobileNumber.setType(AddressBookProto.Person.PhoneType.MOBILE);
        person.addPhones(MobileNumber);

        AddressBookProto.Person.PhoneNumber.Builder HomeNumber = AddressBookProto.Person.PhoneNumber.newBuilder().setNumber("10086");
        HomeNumber.setType(AddressBookProto.Person.PhoneType.HOME);
        person.addPhones(HomeNumber);

        byte[] result = person.build().toByteArray();
        System.out.println("byte array len: " + result.length);

        try {
            AddressBookProto.Person PersonCopy = AddressBookProto.Person.parseFrom(result);

            System.out.println("Person ID: " + PersonCopy.getId());
            System.out.println("Name: " + PersonCopy.getName());
            System.out.println("Email: " + PersonCopy.getEmail());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    private static void AnyTest() {
        AnyProto.Base.Builder base = AnyProto.Base.newBuilder();
        base.setType(AnyProto.Type.FACE);
        base.setPageNumber(2);
        base.setResultPerPage(666);

        AnyProto.Face.Builder face = AnyProto.Face.newBuilder();
        face.setName("guci");

        AnyProto.Plate.Builder plate = AnyProto.Plate.newBuilder();
        plate.setEmail("pra@163.com");

        Any any = Any.pack(face.build());
        base.addObject(any);

        byte[] result = base.build().toByteArray();
        try {
            AnyProto.Base BaseCopy = AnyProto.Base.parseFrom(result);

            System.out.println("page number: " + BaseCopy.getPageNumber());
            for (Any item : BaseCopy.getObjectList()) {
                if (item.is(AnyProto.Face.class)) {
                    System.out.println("yes! a face type!");
                    AnyProto.Face face_copy = item.unpack(AnyProto.Face.class);
                    System.out.println("get value from this any type: " + face_copy.getName());
                }
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }


    private static void OneOfTest() {
        OneOfProto.MyMessage.Builder msg = OneOfProto.MyMessage.newBuilder();
        msg.setUid(666);

        OneOfProto.MsgType1.Builder type1 = OneOfProto.MsgType1.newBuilder();
        type1.setValue(1);

        OneOfProto.MsgType3.Builder type3 = OneOfProto.MsgType3.newBuilder();
        type3.setValue1(11);
        type3.setValue2(12);

        msg.setMsg1(type1);
        msg.setMsg3(type3);

        byte[] result = msg.build().toByteArray();
        try {
            OneOfProto.MyMessage msg_copy = OneOfProto.MyMessage.parseFrom(result);
            //get a value which has not been set
            System.out.println("PID: " + msg_copy.getPid());

            if (msg_copy.hasMsg3()) {
                System.out.print("has msg3: ");
                System.out.print("value 1: " + msg_copy.getMsg3().getValue1());
                System.out.println("  value 3: " + msg_copy.getMsg3().getValue2());
            }
            if (msg_copy.hasMsg1()) {
                System.out.println("has msg1!");
            } else {
                System.out.println("does not have msg1!");
            }

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

    }

    private static void MapTest() {
        MapProto.Man.Builder man = MapProto.Man.newBuilder();
        man.putSkills("C++", "master");
        man.putSkills("Java", "excellent");
        man.putSkills("big data", "nice");

        byte[] result = man.build().toByteArray();
        try {
            MapProto.Man manCopy = MapProto.Man.parseFrom(result);

            System.out.println("value: " + manCopy.getSkillsMap().get("C++"));
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    private static void FromAndToJson() {
        MapProto.Man.Builder man = MapProto.Man.newBuilder();

        man.setName("张三");

        man.putSkills("C++", "master");
        man.putSkills("Java", "excellent");
        man.putSkills("big data", "nice");

        try {
            //   JsonFormat.printer();
            String JsonStr = JsonFormat.printer().print(man);
            System.out.println("json: " + JsonStr);

            MapProto.Man.Builder manCopy = MapProto.Man.newBuilder();
            JsonFormat.parser().merge(JsonStr, manCopy);
            System.out.println("value: " + manCopy.getSkillsMap().get("C++"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

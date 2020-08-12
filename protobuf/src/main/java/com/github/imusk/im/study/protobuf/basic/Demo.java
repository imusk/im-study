package com.github.imusk.im.study.protobuf.basic;

import com.github.imusk.im.study.proto.AddressBookProtos;
import com.github.imusk.im.study.proto.AnyProtos;
import com.github.imusk.im.study.proto.MapProtos;
import com.github.imusk.im.study.proto.OneOfProtos;
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
        AddressBookProtos.Person.Builder person = AddressBookProtos.Person.newBuilder();

        person.setId(1);
        person.setName("Lilei");
        person.setEmail("fqyang@163.com");

        AddressBookProtos.Person.PhoneNumber.Builder MobileNumber =
                AddressBookProtos.Person.PhoneNumber.newBuilder().setNumber("4099134756");
        MobileNumber.setType(AddressBookProtos.Person.PhoneType.MOBILE);
        person.addPhones(MobileNumber);

        AddressBookProtos.Person.PhoneNumber.Builder HomeNumber =
                AddressBookProtos.Person.PhoneNumber.newBuilder().setNumber("10086");
        HomeNumber.setType(AddressBookProtos.Person.PhoneType.HOME);
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
        String MessageFile = "C:\\D_study\\Protobuf\\exercise\\demo1.txt";
        // Read the existing address book.
        try {
            AddressBookProtos.Person person =
                    AddressBookProtos.Person.parseFrom(new FileInputStream(MessageFile));

            System.out.println("Person ID: " + person.getId());
            System.out.println("Name: " + person.getName());
            System.out.println("Email: " + person.getEmail());

            for (AddressBookProtos.Person.PhoneNumber phoneNumber : person.getPhonesList()) {
                switch (phoneNumber.getType()) {
                    case AddressBookProtos.Person.PhoneType.MOBILE:
                        System.out.print("  Mobile phone #: ");
                        break;
                    case AddressBookProtos.Person.PhoneType.HOME:
                        System.out.print("  Home phone #: ");
                        break;
                    case AddressBookProtos.Person.PhoneType.WORK:
                        System.out.print("  Work phone #: ");
                        break;
                }
                System.out.println(phoneNumber.getNumber());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void FromAndToByteArray() {
        AddressBookProtos.Person.Builder person = AddressBookProtos.Person.newBuilder();

        person.setId(1);
        person.setName("Lilei");
        person.setEmail("fqyang@163.com");

        AddressBookProtos.Person.PhoneNumber.Builder MobileNumber =
                AddressBookProtos.Person.PhoneNumber.newBuilder().setNumber("4099134756");
        MobileNumber.setType(AddressBookProtos.Person.PhoneType.MOBILE);
        person.addPhones(MobileNumber);

        AddressBookProtos.Person.PhoneNumber.Builder HomeNumber =
                AddressBookProtos.Person.PhoneNumber.newBuilder().setNumber("10086");
        HomeNumber.setType(AddressBookProtos.Person.PhoneType.HOME);
        person.addPhones(HomeNumber);

        byte[] result = person.build().toByteArray();
        System.out.println("byte array len: " + result.length);

        try {
            AddressBookProtos.Person PersonCopy = AddressBookProtos.Person.parseFrom(result);

            System.out.println("Person ID: " + PersonCopy.getId());
            System.out.println("Name: " + PersonCopy.getName());
            System.out.println("Email: " + PersonCopy.getEmail());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    private static void AnyTest() {
        AnyProtos.Base.Builder base = AnyProtos.Base.newBuilder();
        base.setType(AnyProtos.Type.FACE);
        base.setPageNumber(2);
        base.setResultPerPage(666);

        AnyProtos.Face.Builder face = AnyProtos.Face.newBuilder();
        face.setName("guci");

        AnyProtos.Plate.Builder plate = AnyProtos.Plate.newBuilder();
        plate.setEmail("pra@163.com");

        Any any = Any.pack(face.build());
        base.addObject(any);

        byte[] result = base.build().toByteArray();
        try {
            AnyProtos.Base BaseCopy = AnyProtos.Base.parseFrom(result);

            System.out.println("page number: " + BaseCopy.getPageNumber());
            for (Any item : BaseCopy.getObjectList()) {
                if (item.is(AnyProtos.Face.class)) {
                    System.out.println("yes! a face type!");
                    AnyProtos.Face face_copy = item.unpack(AnyProtos.Face.class);
                    System.out.println("get value from this any type: " + face_copy.getName());
                }
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }


    private static void OneOfTest() {
        OneOfProtos.MyMessage.Builder msg = OneOfProtos.MyMessage.newBuilder();
        msg.setUid(666);

        OneOfProtos.MsgType1.Builder type1 = OneOfProtos.MsgType1.newBuilder();
        type1.setValue(1);

        OneOfProtos.MsgType3.Builder type3 = OneOfProtos.MsgType3.newBuilder();
        type3.setValue1(11);
        type3.setValue2(12);

        msg.setMsg1(type1);
        msg.setMsg3(type3);

        byte[] result = msg.build().toByteArray();
        try {
            OneOfProtos.MyMessage msg_copy = OneOfProtos.MyMessage.parseFrom(result);
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
        MapProtos.Man.Builder man = MapProtos.Man.newBuilder();
        man.putSkills("C++", "master");
        man.putSkills("Java", "excellent");
        man.putSkills("big data", "nice");

        byte[] result = man.build().toByteArray();
        try {
            MapProtos.Man manCopy = MapProtos.Man.parseFrom(result);

            System.out.println("value: " + manCopy.getSkillsMap().get("C++"));
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    private static void FromAndToJson() {
        MapProtos.Man.Builder man = MapProtos.Man.newBuilder();

        man.setName("张三");

        man.putSkills("C++", "master");
        man.putSkills("Java", "excellent");
        man.putSkills("big data", "nice");

        try {
            //   JsonFormat.printer();
            String JsonStr = JsonFormat.printer().print(man);
            System.out.println("json: " + JsonStr);

            MapProtos.Man.Builder manCopy = MapProtos.Man.newBuilder();
            JsonFormat.parser().merge(JsonStr, manCopy);
            System.out.println("value: " + manCopy.getSkillsMap().get("C++"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

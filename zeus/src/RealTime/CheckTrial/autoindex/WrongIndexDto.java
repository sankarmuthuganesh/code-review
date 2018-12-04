//package RealTime.CheckTrial.autoindex;
//
//
//@lombok.Data
//@AutoIndex(FirstBaseDto.class)
//@Join(with = IndexOnIndexJoinDto.class, as = "j", where = {"id == #j.id", "value == #j.value", "name == #j.name","status == #j.status"})
//public class WrongIndexDto {
//    @Key(order = 0)
//    private int sequence;
//
//    @Key(order = 1)
//    @Column
//    private String id;
//}

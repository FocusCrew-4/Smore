package com.smore.seller.application.command;

public record ApplyCommand(
    Long requesterId,
    String accountNum
) {

}

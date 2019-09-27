function valid = check_TOWKNOWNstate(state)

    if bitand(state, 2^14)
        valid = 1;
    else
        valid = 0;
    end
end
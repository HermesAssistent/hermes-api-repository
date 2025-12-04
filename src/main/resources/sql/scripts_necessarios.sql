CREATE UNIQUE INDEX uq_chat_session_user_ativo
    ON chat_session(user_id)
    WHERE ativo = true;

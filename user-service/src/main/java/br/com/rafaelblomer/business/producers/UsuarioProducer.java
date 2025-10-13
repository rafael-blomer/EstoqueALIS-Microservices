package br.com.rafaelblomer.business.producers;

import br.com.rafaelblomer.business.dtos.PublicacaoEmailDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UsuarioProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value(value = "${broker.queue.email.name}")
    private String routingKeyVerificacao;

    @Value(value = "${broker.queue.email.trocasenha}")
    private String routingKeyTrocaSenha;

    public void publicarMensagemEmailConfirmacao(PublicacaoEmailDTO publicacaoEmailDTO) {
        rabbitTemplate.convertAndSend(routingKeyVerificacao, publicacaoEmailDTO);
    }

    public void publicarMensagemTrocaSenha(PublicacaoEmailDTO publicacaoEmailDTO) {
        rabbitTemplate.convertAndSend(routingKeyTrocaSenha, publicacaoEmailDTO);
    }
}

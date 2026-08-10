# tracking-service

Serviço de Rastreamento do RotaHub. Dono da posição/status da entrega em tempo real.

- **Stack:** Java 21 + Spring Boot 3 (Spring Web, Spring Data MongoDB, Bean Validation, Testcontainers)
- **Banco:** MongoDB (próprio, não compartilhado com outros serviços)
- **Comunicação:**
  - Expõe REST síncrono, consumido pelo BFF (`rotahub-bff`)
  - Publica o evento assíncrono `entrega.finalizada` via RabbitMQ quando o status de uma entrega vira `ENTREGUE`, consumido pelo `orders-service`

Contratos completos (endpoints e payload de eventos) estão documentados em `rotahub-infra/docs/contracts.md`.

## Status do rastreio

`AGUARDANDO_COLETA → COLETADO → EM_TRANSITO → SAIU_PARA_ENTREGA → ENTREGUE` (ou `TENTATIVA_FALHA`)

## Rodando localmente

> TODO: preencher quando o projeto Spring Boot for gerado.

package com.stripe.android.model

import com.google.common.truth.Truth.assertThat
import com.stripe.android.CardNumberFixtures.VISA_NO_SPACES
import kotlin.test.Test

/**
 * Test class for [SourceParams].
 */
class SourceParamsTest {

    @Test
    fun createAlipayReusableParams_withAllFields_hasExpectedFields() {
        val params = SourceParams.createAlipayReusableParams(
            "usd",
            "Jean Valjean",
            "jdog@lesmis.net",
            RETURN_URL
        )

        assertThat(params.type)
            .isEqualTo(Source.SourceType.ALIPAY)
        assertThat(params.usage)
            .isEqualTo(Source.Usage.Reusable)
        assertThat(params.amount)
            .isNull()
        assertThat(params.currency)
            .isEqualTo("usd")
        assertThat(params.returnUrl)
            .isEqualTo(RETURN_URL)

        assertThat(
            requireNotNull(params.owner)
        ).isEqualTo(
            SourceParams.OwnerParams(
                name = "Jean Valjean",
                email = "jdog@lesmis.net"
            )
        )
    }

    @Test
    fun createAlipayReusableParams_withOnlyName_hasOnlyExpectedFields() {
        val params = SourceParams.createAlipayReusableParams(
            currency = "cad",
            name = "Hari Seldon",
            returnUrl = RETURN_URL
        )

        assertThat(params.type)
            .isEqualTo(Source.SourceType.ALIPAY)
        assertThat(params.usage)
            .isEqualTo(Source.Usage.Reusable)
        assertThat(params.amount)
            .isNull()
        assertThat(params.currency)
            .isEqualTo("cad")
        assertThat(params.returnUrl)
            .isEqualTo(RETURN_URL)

        assertThat(
            requireNotNull(params.owner)
        ).isEqualTo(
            SourceParams.OwnerParams(
                name = "Hari Seldon",
            )
        )
    }

    @Test
    fun createAlipaySingleUseParams_withAllFields_hasExpectedFields() {
        val params = SourceParams.createAlipaySingleUseParams(
            AMOUNT,
            "aud",
            "Jane Tester",
            "jane@test.com",
            RETURN_URL
        )

        assertThat(params.type)
            .isEqualTo(Source.SourceType.ALIPAY)
        assertThat(params.amount)
            .isEqualTo(AMOUNT)
        assertThat(params.currency)
            .isEqualTo("aud")

        assertThat(
            requireNotNull(params.owner)
        ).isEqualTo(
            SourceParams.OwnerParams(
                name = "Jane Tester",
                email = "jane@test.com"
            )
        )

        assertThat(params.returnUrl)
            .isEqualTo(RETURN_URL)
    }

    @Test
    fun createAlipaySingleUseParams_withoutOwner_hasNoOwnerFields() {
        val params = SourceParams.createAlipaySingleUseParams(
            amount = AMOUNT,
            currency = "eur",
            returnUrl = RETURN_URL
        )

        assertThat(params.type)
            .isEqualTo(Source.SourceType.ALIPAY)
        assertThat(params.amount)
            .isEqualTo(AMOUNT)
        assertThat(params.currency)
            .isEqualTo("eur")

        assertThat(
            requireNotNull(params.owner)
        ).isEqualTo(
            SourceParams.OwnerParams()
        )

        assertThat(params.returnUrl)
            .isEqualTo(RETURN_URL)
    }

    @Test
    fun createBancontactParams_hasExpectedFields() {
        val params = SourceParams.createBancontactParams(
            AMOUNT,
            name = "Stripe",
            returnUrl = RETURN_URL,
            statementDescriptor = "descriptor",
            preferredLanguage = "en"
        )

        assertThat(params.type)
            .isEqualTo(Source.SourceType.BANCONTACT)
        assertThat(params.currency)
            .isEqualTo("eur")
        assertThat(params.amount)
            .isEqualTo(AMOUNT)
        assertThat(
            requireNotNull(params.owner)
        ).isEqualTo(
            SourceParams.OwnerParams(
                name = "Stripe"
            )
        )
        assertThat(params.returnUrl)
            .isEqualTo(RETURN_URL)

        assertThat(
            requireNotNull(params.apiParameterMap)
        ).isEqualTo(
            mapOf(
                "statement_descriptor" to "descriptor",
                "preferred_language" to "en"
            )
        )
    }

    @Test
    fun createBancontactParams_toParamMap_createsExpectedMap() {
        val params = SourceParams.createBancontactParams(
            AMOUNT,
            "Stripe",
            RETURN_URL,
            "descriptor",
            "en"
        )

        assertThat(
            params.toParamMap()
        ).isEqualTo(
            mapOf(
                "type" to Source.SourceType.BANCONTACT,
                "currency" to Source.EURO,
                "amount" to AMOUNT,
                "owner" to mapOf("name" to "Stripe"),
                "redirect" to mapOf("return_url" to RETURN_URL),
                Source.SourceType.BANCONTACT to mapOf(
                    "statement_descriptor" to "descriptor",
                    "preferred_language" to "en"
                )
            )
        )
    }

    @Test
    fun createBancontactParams_hasExpectedFields_optionalStatementDescriptor() {
        val params = SourceParams.createBancontactParams(
            AMOUNT,
            "Stripe",
            RETURN_URL,
            null,
            "en"
        )

        assertThat(
            requireNotNull(params.apiParameterMap)
        ).isEqualTo(
            mapOf(
                "preferred_language" to "en"
            )
        )
    }

    @Test
    fun createBancontactParams_hasExpectedFields_optionalPreferredLanguage() {
        val params = SourceParams.createBancontactParams(
            AMOUNT,
            "Stripe",
            RETURN_URL,
            "descriptor",
            null
        )

        assertThat(
            requireNotNull(params.apiParameterMap)
        ).isEqualTo(
            mapOf(
                "statement_descriptor" to "descriptor"
            )
        )
    }

    @Test
    fun createBancontactParams_hasExpectedFields_optionalEverything() {
        val params = SourceParams.createBancontactParams(
            AMOUNT,
            "Stripe",
            RETURN_URL,
            null,
            null
        )

        assertThat(params.apiParameterMap)
            .isNull()
    }

    @Test
    fun `create with CardParams object should return expected map`() {
        assertThat(
            SourceParams.createCardParams(CardParamsFixtures.DEFAULT)
                .toParamMap()
        ).isEqualTo(
            mapOf(
                "type" to "card",
                "card" to mapOf(
                    "number" to VISA_NO_SPACES,
                    "exp_month" to 12,
                    "exp_year" to 2025,
                    "cvc" to "123"
                ),
                "owner" to mapOf(
                    "address" to mapOf(
                        "line1" to "123 Market St",
                        "line2" to "#345",
                        "city" to "San Francisco",
                        "state" to "CA",
                        "postal_code" to "94107",
                        "country" to "US"
                    ),
                    "name" to "Jenny Rosen"
                ),
                "metadata" to mapOf("fruit" to "orange")
            )
        )
    }

    @Test
    fun `create with Card object should return expected map`() {
        assertThat(
            SourceParams.createCardParams(CardFixtures.CARD)
                .toParamMap()
        ).isEqualTo(
            mapOf(
                "type" to "card",
                "card" to mapOf(
                    "number" to VISA_NO_SPACES,
                    "exp_month" to 8,
                    "exp_year" to 2050,
                    "cvc" to "123"
                ),
                "owner" to mapOf(
                    "address" to mapOf(
                        "line1" to "123 Market St",
                        "line2" to "#345",
                        "city" to "San Francisco",
                        "state" to "CA",
                        "postal_code" to "94107",
                        "country" to "US"
                    ),
                    "name" to "Jenny Rosen"
                )
            )
        )
    }

    @Test
    fun createCardParamsFromGooglePay_withNoBillingAddress() {
        assertThat(
            SourceParams.createCardParamsFromGooglePay(
                GooglePayFixtures.GOOGLE_PAY_RESULT_WITH_NO_BILLING_ADDRESS
            )
        ).isEqualTo(
            SourceParams
                .createSourceFromTokenParams(
                    "tok_1F4ACMCRMbs6FrXf6fPqLnN7",
                    setOf("GooglePay")
                )
                .setOwner(SourceParams.OwnerParams())
        )
    }

    @Test
    fun createCardParamsFromGooglePay_withFullBillingAddress() {
        assertThat(
            SourceParams.createCardParamsFromGooglePay(
                GooglePayFixtures.GOOGLE_PAY_RESULT_WITH_FULL_BILLING_ADDRESS
            )
        ).isEqualTo(
            SourceParams.createSourceFromTokenParams(
                "tok_1F4VSjBbvEcIpqUbSsbEtBap",
                attribution = setOf("GooglePay")
            ).setOwner(
                SourceParams.OwnerParams(
                    email = "stripe@example.com",
                    name = "Stripe Johnson",
                    phone = "1-888-555-1234",
                    address = Address(
                        line1 = "510 Townsend St",
                        city = "San Francisco",
                        state = "CA",
                        postalCode = "94103",
                        country = "US"
                    )
                )
            )
        )
    }

    @Test
    fun createEPSParams_hasExpectedFields() {
        val params = SourceParams.createEPSParams(
            AMOUNT,
            "Stripe",
            RETURN_URL,
            "stripe descriptor"
        )

        assertThat(params.type)
            .isEqualTo(Source.SourceType.EPS)
        assertThat(params.currency)
            .isEqualTo("eur")
        assertThat(
            requireNotNull(params.owner)
        ).isEqualTo(
            SourceParams.OwnerParams(
                name = "Stripe"
            )
        )
        assertThat(params.returnUrl)
            .isEqualTo(RETURN_URL)

        assertThat(
            requireNotNull(params.apiParameterMap)
        ).isEqualTo(
            mapOf(
                "statement_descriptor" to "stripe descriptor"
            )
        )
    }

    @Test
    fun createEPSParams_toParamMap_createsExpectedMap() {
        val params = SourceParams.createEPSParams(
            AMOUNT,
            "Stripe",
            RETURN_URL,
            "stripe descriptor"
        )

        assertThat(
            params.toParamMap()
        ).isEqualTo(
            mapOf(
                "type" to Source.SourceType.EPS,
                "currency" to Source.EURO,
                "amount" to AMOUNT,
                "owner" to mapOf("name" to "Stripe"),
                "redirect" to mapOf("return_url" to RETURN_URL),
                Source.SourceType.EPS to mapOf("statement_descriptor" to "stripe descriptor")
            )
        )
    }

    @Test
    fun createEPSParams_toParamMap_createsExpectedMap_noStatementDescriptor() {
        val params = SourceParams.createEPSParams(
            AMOUNT,
            "Stripe",
            RETURN_URL,
            null
        )

        assertThat(
            params.toParamMap()
        ).isEqualTo(
            mapOf(
                "type" to Source.SourceType.EPS,
                "currency" to Source.EURO,
                "amount" to AMOUNT,
                "owner" to mapOf("name" to "Stripe"),
                "redirect" to mapOf("return_url" to RETURN_URL)
            )
        )
    }

    @Test
    fun createGiropayParams_hasExpectedFields() {
        val params = SourceParams.createGiropayParams(
            AMOUNT,
            "Stripe",
            RETURN_URL,
            "stripe descriptor"
        )

        assertThat(params.type)
            .isEqualTo(Source.SourceType.GIROPAY)
        assertThat(params.currency)
            .isEqualTo("eur")
        assertThat(params.amount)
            .isEqualTo(AMOUNT)

        assertThat(
            requireNotNull(params.owner)
        ).isEqualTo(
            SourceParams.OwnerParams(
                name = "Stripe"
            )
        )

        assertThat(params.returnUrl)
            .isEqualTo(RETURN_URL)

        assertThat(
            requireNotNull(params.apiParameterMap)
        ).isEqualTo(
            mapOf("statement_descriptor" to "stripe descriptor")
        )
    }

    @Test
    fun createGiropayParams_toParamMap_createsExpectedMap() {
        val params = SourceParams.createGiropayParams(
            AMOUNT,
            "Stripe",
            RETURN_URL,
            "stripe descriptor"
        )

        assertThat(
            params.toParamMap()
        ).isEqualTo(
            mapOf(
                "type" to Source.SourceType.GIROPAY,
                "currency" to Source.EURO,
                "amount" to AMOUNT,
                "owner" to mapOf(
                    "name" to "Stripe"
                ),
                "redirect" to mapOf(
                    "return_url" to RETURN_URL
                ),
                Source.SourceType.GIROPAY to mapOf(
                    "statement_descriptor" to "stripe descriptor"
                )
            )
        )
    }

    @Test
    fun createGiropayParams_withNullStatementDescriptor_hasExpectedFieldsButNoApiParams() {
        assertThat(
            SourceParams.createGiropayParams(
                AMOUNT,
                "Stripe",
                RETURN_URL,
                null
            ).toParamMap()
        ).isEqualTo(
            mapOf(
                "type" to "giropay",
                "currency" to "eur",
                "amount" to AMOUNT,
                "owner" to mapOf(
                    "name" to "Stripe"
                ),
                "redirect" to mapOf(
                    "return_url" to RETURN_URL
                )
            )
        )
    }

    @Test
    fun createIdealParams_hasExpectedFields() {
        val params = SourceParams.createIdealParams(
            AMOUNT,
            "Default Name",
            RETURN_URL,
            "something you bought",
            "SVB"
        )
        assertThat(params.type)
            .isEqualTo(Source.SourceType.IDEAL)
        assertThat(params.currency)
            .isEqualTo("eur")
        assertThat(params.amount)
            .isEqualTo(AMOUNT)

        assertThat(
            requireNotNull(params.owner)
        ).isEqualTo(
            SourceParams.OwnerParams(
                name = "Default Name"
            )
        )

        assertThat(params.returnUrl)
            .isEqualTo(RETURN_URL)

        assertThat(
            requireNotNull(params.apiParameterMap)
        ).isEqualTo(
            mapOf(
                "bank" to "SVB",
                "statement_descriptor" to "something you bought"
            )
        )
    }

    @Test
    fun createIdealParams_toParamMap_createsExpectedMap() {
        val params = SourceParams.createIdealParams(
            AMOUNT,
            "Default Name",
            RETURN_URL,
            "something you bought",
            "SVB"
        )

        assertThat(params.toParamMap())
            .isEqualTo(
                mapOf(
                    "type" to "ideal",
                    "currency" to "eur",
                    "amount" to AMOUNT,
                    "owner" to mapOf("name" to "Default Name"),
                    "redirect" to mapOf("return_url" to RETURN_URL),
                    "ideal" to mapOf(
                        "statement_descriptor" to "something you bought",
                        "bank" to "SVB"
                    )
                )
            )
    }

    @Test
    fun createP24Params_withAllFields_hasExpectedFields() {
        val params = SourceParams.createP24Params(
            AMOUNT,
            "eur",
            "Jane Tester",
            "jane@test.com",
            RETURN_URL
        )

        assertThat(params.type)
            .isEqualTo(Source.SourceType.P24)
        assertThat(params.amount)
            .isEqualTo(AMOUNT)
        assertThat(params.currency)
            .isEqualTo("eur")

        assertThat(
            requireNotNull(params.owner)
        ).isEqualTo(
            SourceParams.OwnerParams(
                name = "Jane Tester",
                email = "jane@test.com"
            )
        )

        assertThat(params.returnUrl)
            .isEqualTo(RETURN_URL)
    }

    @Test
    fun createP24Params_withNullName_hasExpectedFields() {
        val params = SourceParams.createP24Params(
            AMOUNT,
            "eur",
            null,
            "jane@test.com",
            RETURN_URL
        )

        assertThat(params.type)
            .isEqualTo(Source.SourceType.P24)
        assertThat(params.amount)
            .isEqualTo(AMOUNT)
        assertThat(params.currency)
            .isEqualTo("eur")

        assertThat(
            requireNotNull(params.owner)
        ).isEqualTo(
            SourceParams.OwnerParams(
                email = "jane@test.com"
            )
        )

        assertThat(params.returnUrl)
            .isEqualTo(RETURN_URL)
    }

    @Test
    fun createMultibancoParams_hasExpectedFields() {
        val params = SourceParams.createMultibancoParams(
            AMOUNT,
            RETURN_URL,
            "multibancoholder@stripe.com"
        )

        assertThat(params.type)
            .isEqualTo(Source.SourceType.MULTIBANCO)
        assertThat(params.currency)
            .isEqualTo("eur")
        assertThat(params.amount)
            .isEqualTo(AMOUNT)
        assertThat(params.returnUrl)
            .isEqualTo(RETURN_URL)

        assertThat(
            requireNotNull(params.owner)
        ).isEqualTo(
            SourceParams.OwnerParams(
                email = "multibancoholder@stripe.com"
            )
        )
    }

    @Test
    fun createMultibancoParams_toParamMap_createsExpectedMap() {
        val params = SourceParams.createMultibancoParams(
            AMOUNT,
            RETURN_URL,
            "multibancoholder@stripe.com"
        )

        assertThat(
            params.toParamMap()
        ).isEqualTo(
            mapOf(
                "type" to Source.SourceType.MULTIBANCO,
                "currency" to Source.EURO,
                "amount" to AMOUNT,
                "owner" to mapOf("email" to "multibancoholder@stripe.com"),
                "redirect" to mapOf("return_url" to RETURN_URL)
            )
        )
    }

    @Test
    fun createSepaDebitParams_hasExpectedFields() {
        val params = SourceParams.createSepaDebitParams(
            "Jai Testa",
            "ibaniban",
            "sepaholder@stripe.com",
            "44 Fourth Street",
            "Test City",
            "90210",
            "EI"
        )

        assertThat(params.type)
            .isEqualTo(Source.SourceType.SEPA_DEBIT)
        assertThat(params.currency)
            .isEqualTo(Source.EURO)

        assertThat(
            requireNotNull(params.owner)
        ).isEqualTo(
            SourceParams.OwnerParams(
                name = "Jai Testa",
                email = "sepaholder@stripe.com",
                address = Address(
                    line1 = "44 Fourth Street",
                    city = "Test City",
                    postalCode = "90210",
                    country = "EI"
                )
            )
        )

        assertThat(
            requireNotNull(params.apiParameterMap)
        ).isEqualTo(
            mapOf("iban" to "ibaniban")
        )
    }

    @Test
    fun createSepaDebitParams_toParamMap_createsExpectedMap() {
        val params = SourceParams.createSepaDebitParams(
            "Jai Testa",
            "ibaniban",
            "sepaholder@stripe.com",
            "44 Fourth Street",
            "Test City",
            "90210",
            "EI"
        )

        assertThat(params.type)
            .isEqualTo(Source.SourceType.SEPA_DEBIT)

        assertThat(
            params.toParamMap()
        ).isEqualTo(
            mapOf(
                "type" to Source.SourceType.SEPA_DEBIT,
                "currency" to Source.EURO,
                "owner" to mapOf(
                    "name" to "Jai Testa",
                    "email" to "sepaholder@stripe.com",
                    "address" to mapOf(
                        "line1" to "44 Fourth Street",
                        "city" to "Test City",
                        "postal_code" to "90210",
                        "country" to "EI"
                    )
                ),
                Source.SourceType.SEPA_DEBIT to mapOf("iban" to "ibaniban")
            )
        )
    }

    @Test
    fun createSofortParams_hasExpectedFields() {
        val params = SourceParams.createSofortParams(
            AMOUNT,
            RETURN_URL,
            "UK",
            "a thing you bought"
        )

        assertThat(params.type)
            .isEqualTo(Source.SourceType.SOFORT)
        assertThat(params.currency)
            .isEqualTo("eur")
        assertThat(params.amount)
            .isEqualTo(AMOUNT)
        assertThat(params.returnUrl)
            .isEqualTo(RETURN_URL)

        assertThat(
            requireNotNull(params.apiParameterMap)
        ).isEqualTo(
            mapOf(
                "country" to "UK",
                "statement_descriptor" to "a thing you bought"
            )
        )
    }

    @Test
    fun createSofortParams_toParamMap_createsExpectedMap() {
        val params = SourceParams.createSofortParams(
            AMOUNT,
            RETURN_URL,
            "UK",
            "a thing you bought"
        )

        assertThat(
            params.toParamMap()
        ).isEqualTo(
            mapOf(
                "type" to Source.SourceType.SOFORT,
                "currency" to Source.EURO,
                "amount" to AMOUNT,
                "redirect" to mapOf("return_url" to RETURN_URL),
                Source.SourceType.SOFORT to mapOf(
                    "country" to "UK",
                    "statement_descriptor" to "a thing you bought"
                )
            )
        )
    }

    @Test
    fun createThreeDSecureParams_hasExpectedFields() {
        val params = SourceParams.createThreeDSecureParams(
            AMOUNT,
            "brl",
            RETURN_URL,
            "card_id_123"
        )

        assertThat(params.type)
            .isEqualTo(Source.SourceType.THREE_D_SECURE)
        // Brazilian Real
        assertThat(params.currency)
            .isEqualTo("brl")
        assertThat(params.amount)
            .isEqualTo(AMOUNT)
        assertThat(params.returnUrl)
            .isEqualTo(RETURN_URL)

        assertThat(
            requireNotNull(params.apiParameterMap)
        ).isEqualTo(
            mapOf(
                "card" to "card_id_123"
            )
        )
    }

    @Test
    fun createThreeDSecureParams_toParamMap_createsExpectedMap() {
        val params = SourceParams.createThreeDSecureParams(
            AMOUNT,
            "brl",
            RETURN_URL,
            "card_id_123"
        )

        assertThat(
            params.toParamMap()
        ).isEqualTo(
            mapOf(
                "type" to Source.SourceType.THREE_D_SECURE,
                "currency" to "brl",
                "amount" to AMOUNT,
                "redirect" to mapOf("return_url" to RETURN_URL),
                Source.SourceType.THREE_D_SECURE to mapOf("card" to "card_id_123")
            )
        )
    }

    @Test
    fun createCustomParamsWithSourceTypeParameters_toParamMap_createsExpectedMap() {
        // Using the Giropay constructor to add some free params and expected values,
        // including a source type params
        val dogecoin = "dogecoin"

        val dogecoinParams = mapOf("statement_descriptor" to "stripe descriptor")

        val params = SourceParams.createCustomParams(dogecoin)
            .setCurrency(Source.EURO)
            .setAmount(AMOUNT)
            .setReturnUrl(RETURN_URL)
            .setOwner(SourceParams.OwnerParams(name = "Stripe"))
            .setApiParameterMap(dogecoinParams)

        assertThat(
            params.toParamMap()
        ).isEqualTo(
            mapOf(
                "type" to dogecoin,
                "currency" to Source.EURO,
                "amount" to AMOUNT,
                "owner" to mapOf("name" to "Stripe"),
                "redirect" to mapOf("return_url" to RETURN_URL),
                dogecoin to mapOf("statement_descriptor" to "stripe descriptor")
            )
        )
    }

    @Test
    fun createWeChatPayParams_shouldCreateExpectedParams() {
        val params = SourceParams
            .createWeChatPayParams(
                AMOUNT,
                "USD",
                "wxa0df51ec63e578ce",
                "WIDGET STORE"
            )

        assertThat(
            params.toParamMap()
        ).isEqualTo(
            mapOf(
                "type" to "wechat",
                "amount" to AMOUNT,
                "currency" to "USD",
                "wechat" to mapOf(
                    "appid" to "wxa0df51ec63e578ce",
                    "statement_descriptor" to "WIDGET STORE"
                )
            )
        )
    }

    @Test
    fun setCustomType_forEmptyParams_setsTypeToUnknown() {
        val params = SourceParams.createCustomParams("dogecoin")
        assertThat(params.type)
            .isEqualTo(Source.SourceType.UNKNOWN)
        assertThat(params.typeRaw)
            .isEqualTo("dogecoin")
    }

    @Test
    fun createCustomParams_withCustomType() {
        val params = SourceParams.createCustomParams("bar_tab")
            .setAmount(AMOUNT)
            .setCurrency("brl")
            .setReturnUrl(RETURN_URL)
            .setApiParameterMap(
                mapOf("card" to "card_id_123")
            )

        assertThat(
            params.toParamMap()
        ).isEqualTo(
            mapOf(
                "type" to "bar_tab",
                "amount" to AMOUNT,
                "currency" to "brl",
                "redirect" to mapOf("return_url" to RETURN_URL),
                "bar_tab" to mapOf("card" to "card_id_123")
            )
        )
    }

    private companion object {
        private const val AMOUNT = 1099L
        private const val RETURN_URL = "stripe://return"
    }
}

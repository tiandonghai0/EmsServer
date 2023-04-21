package com.shmet.protobuf.gen;

public final class LoginProto {
    private LoginProto() {
    }

    public static void registerAllExtensions(
            com.google.protobuf.ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(
            com.google.protobuf.ExtensionRegistry registry) {
        registerAllExtensions(
                (com.google.protobuf.ExtensionRegistryLite) registry);
    }

    public interface ProtoLoginInfoOrBuilder extends
            com.google.protobuf.MessageOrBuilder {

        /**
         * <code>string uname = 1;</code>
         */
        java.lang.String getUname();

        /**
         * <code>string uname = 1;</code>
         */
        com.google.protobuf.ByteString
        getUnameBytes();

        /**
         * <code>string pwd = 2;</code>
         */
        java.lang.String getPwd();

        /**
         * <code>string pwd = 2;</code>
         */
        com.google.protobuf.ByteString
        getPwdBytes();

        /**
         * <code>string acconeId = 3;</code>
         */
        java.lang.String getAcconeId();

        /**
         * <code>string acconeId = 3;</code>
         */
        com.google.protobuf.ByteString
        getAcconeIdBytes();

        /**
         * <code>repeated string deviceModel = 4;</code>
         */
        java.util.List<java.lang.String>
        getDeviceModelList();

        /**
         * <code>repeated string deviceModel = 4;</code>
         */
        int getDeviceModelCount();

        /**
         * <code>repeated string deviceModel = 4;</code>
         */
        java.lang.String getDeviceModel(int index);

        /**
         * <code>repeated string deviceModel = 4;</code>
         */
        com.google.protobuf.ByteString
        getDeviceModelBytes(int index);

        /**
         * <code>repeated bytes tagCodeCsv = 5;</code>
         */
        java.util.List<com.google.protobuf.ByteString> getTagCodeCsvList();

        /**
         * <code>repeated bytes tagCodeCsv = 5;</code>
         */
        int getTagCodeCsvCount();

        /**
         * <code>repeated bytes tagCodeCsv = 5;</code>
         */
        com.google.protobuf.ByteString getTagCodeCsv(int index);
    }

    /**
     * Protobuf type {@code ProtoLoginInfo}
     */
    public static final class ProtoLoginInfo extends
            com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:ProtoLoginInfo)
            ProtoLoginInfoOrBuilder {
        private static final long serialVersionUID = 0L;

        // Use ProtoLoginInfo.newBuilder() to construct.
        private ProtoLoginInfo(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }

        private ProtoLoginInfo() {
            uname_ = "";
            pwd_ = "";
            acconeId_ = "";
            deviceModel_ = com.google.protobuf.LazyStringArrayList.EMPTY;
            tagCodeCsv_ = java.util.Collections.emptyList();
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
            return this.unknownFields;
        }

        private ProtoLoginInfo(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            if (extensionRegistry == null) {
                throw new java.lang.NullPointerException();
            }
            int mutable_bitField0_ = 0;
            com.google.protobuf.UnknownFieldSet.Builder unknownFields =
                    com.google.protobuf.UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        case 10: {
                            java.lang.String s = input.readStringRequireUtf8();

                            uname_ = s;
                            break;
                        }
                        case 18: {
                            java.lang.String s = input.readStringRequireUtf8();

                            pwd_ = s;
                            break;
                        }
                        case 26: {
                            java.lang.String s = input.readStringRequireUtf8();

                            acconeId_ = s;
                            break;
                        }
                        case 34: {
                            java.lang.String s = input.readStringRequireUtf8();
                            if (!((mutable_bitField0_ & 0x00000008) == 0x00000008)) {
                                deviceModel_ = new com.google.protobuf.LazyStringArrayList();
                                mutable_bitField0_ |= 0x00000008;
                            }
                            deviceModel_.add(s);
                            break;
                        }
                        case 42: {
                            if (!((mutable_bitField0_ & 0x00000010) == 0x00000010)) {
                                tagCodeCsv_ = new java.util.ArrayList<com.google.protobuf.ByteString>();
                                mutable_bitField0_ |= 0x00000010;
                            }
                            tagCodeCsv_.add(input.readBytes());
                            break;
                        }
                        default: {
                            if (!parseUnknownFieldProto3(
                                    input, unknownFields, extensionRegistry, tag)) {
                                done = true;
                            }
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                        e).setUnfinishedMessage(this);
            } finally {
                if (((mutable_bitField0_ & 0x00000008) == 0x00000008)) {
                    deviceModel_ = deviceModel_.getUnmodifiableView();
                }
                if (((mutable_bitField0_ & 0x00000010) == 0x00000010)) {
                    tagCodeCsv_ = java.util.Collections.unmodifiableList(tagCodeCsv_);
                }
                this.unknownFields = unknownFields.build();
                makeExtensionsImmutable();
            }
        }

        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return LoginProto.internal_static_ProtoLoginInfo_descriptor;
        }

        @java.lang.Override
        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return LoginProto.internal_static_ProtoLoginInfo_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            LoginProto.ProtoLoginInfo.class, LoginProto.ProtoLoginInfo.Builder.class);
        }

        private int bitField0_;
        public static final int UNAME_FIELD_NUMBER = 1;
        private volatile java.lang.Object uname_;

        /**
         * <code>string uname = 1;</code>
         */
        public java.lang.String getUname() {
            java.lang.Object ref = uname_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                uname_ = s;
                return s;
            }
        }

        /**
         * <code>string uname = 1;</code>
         */
        public com.google.protobuf.ByteString
        getUnameBytes() {
            java.lang.Object ref = uname_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                uname_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        public static final int PWD_FIELD_NUMBER = 2;
        private volatile java.lang.Object pwd_;

        /**
         * <code>string pwd = 2;</code>
         */
        public java.lang.String getPwd() {
            java.lang.Object ref = pwd_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                pwd_ = s;
                return s;
            }
        }

        /**
         * <code>string pwd = 2;</code>
         */
        public com.google.protobuf.ByteString
        getPwdBytes() {
            java.lang.Object ref = pwd_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                pwd_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        public static final int ACCONEID_FIELD_NUMBER = 3;
        private volatile java.lang.Object acconeId_;

        /**
         * <code>string acconeId = 3;</code>
         */
        public java.lang.String getAcconeId() {
            java.lang.Object ref = acconeId_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                acconeId_ = s;
                return s;
            }
        }

        /**
         * <code>string acconeId = 3;</code>
         */
        public com.google.protobuf.ByteString
        getAcconeIdBytes() {
            java.lang.Object ref = acconeId_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                acconeId_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        public static final int DEVICEMODEL_FIELD_NUMBER = 4;
        private com.google.protobuf.LazyStringList deviceModel_;

        /**
         * <code>repeated string deviceModel = 4;</code>
         */
        public com.google.protobuf.ProtocolStringList
        getDeviceModelList() {
            return deviceModel_;
        }

        /**
         * <code>repeated string deviceModel = 4;</code>
         */
        public int getDeviceModelCount() {
            return deviceModel_.size();
        }

        /**
         * <code>repeated string deviceModel = 4;</code>
         */
        public java.lang.String getDeviceModel(int index) {
            return deviceModel_.get(index);
        }

        /**
         * <code>repeated string deviceModel = 4;</code>
         */
        public com.google.protobuf.ByteString
        getDeviceModelBytes(int index) {
            return deviceModel_.getByteString(index);
        }

        public static final int TAGCODECSV_FIELD_NUMBER = 5;
        private java.util.List<com.google.protobuf.ByteString> tagCodeCsv_;

        /**
         * <code>repeated bytes tagCodeCsv = 5;</code>
         */
        public java.util.List<com.google.protobuf.ByteString>
        getTagCodeCsvList() {
            return tagCodeCsv_;
        }

        /**
         * <code>repeated bytes tagCodeCsv = 5;</code>
         */
        public int getTagCodeCsvCount() {
            return tagCodeCsv_.size();
        }

        /**
         * <code>repeated bytes tagCodeCsv = 5;</code>
         */
        public com.google.protobuf.ByteString getTagCodeCsv(int index) {
            return tagCodeCsv_.get(index);
        }

        private byte memoizedIsInitialized = -1;

        @java.lang.Override
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        @java.lang.Override
        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
            if (!getUnameBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 1, uname_);
            }
            if (!getPwdBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 2, pwd_);
            }
            if (!getAcconeIdBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 3, acconeId_);
            }
            for (int i = 0; i < deviceModel_.size(); i++) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 4, deviceModel_.getRaw(i));
            }
            for (int i = 0; i < tagCodeCsv_.size(); i++) {
                output.writeBytes(5, tagCodeCsv_.get(i));
            }
            unknownFields.writeTo(output);
        }

        @java.lang.Override
        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            if (!getUnameBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, uname_);
            }
            if (!getPwdBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, pwd_);
            }
            if (!getAcconeIdBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, acconeId_);
            }
            {
                int dataSize = 0;
                for (int i = 0; i < deviceModel_.size(); i++) {
                    dataSize += computeStringSizeNoTag(deviceModel_.getRaw(i));
                }
                size += dataSize;
                size += 1 * getDeviceModelList().size();
            }
            {
                int dataSize = 0;
                for (int i = 0; i < tagCodeCsv_.size(); i++) {
                    dataSize += com.google.protobuf.CodedOutputStream
                            .computeBytesSizeNoTag(tagCodeCsv_.get(i));
                }
                size += dataSize;
                size += 1 * getTagCodeCsvList().size();
            }
            size += unknownFields.getSerializedSize();
            memoizedSize = size;
            return size;
        }

        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof LoginProto.ProtoLoginInfo)) {
                return super.equals(obj);
            }
            LoginProto.ProtoLoginInfo other = (LoginProto.ProtoLoginInfo) obj;

            boolean result = true;
            result = result && getUname()
                    .equals(other.getUname());
            result = result && getPwd()
                    .equals(other.getPwd());
            result = result && getAcconeId()
                    .equals(other.getAcconeId());
            result = result && getDeviceModelList()
                    .equals(other.getDeviceModelList());
            result = result && getTagCodeCsvList()
                    .equals(other.getTagCodeCsvList());
            result = result && unknownFields.equals(other.unknownFields);
            return result;
        }

        @java.lang.Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptor().hashCode();
            hash = (37 * hash) + UNAME_FIELD_NUMBER;
            hash = (53 * hash) + getUname().hashCode();
            hash = (37 * hash) + PWD_FIELD_NUMBER;
            hash = (53 * hash) + getPwd().hashCode();
            hash = (37 * hash) + ACCONEID_FIELD_NUMBER;
            hash = (53 * hash) + getAcconeId().hashCode();
            if (getDeviceModelCount() > 0) {
                hash = (37 * hash) + DEVICEMODEL_FIELD_NUMBER;
                hash = (53 * hash) + getDeviceModelList().hashCode();
            }
            if (getTagCodeCsvCount() > 0) {
                hash = (37 * hash) + TAGCODECSV_FIELD_NUMBER;
                hash = (53 * hash) + getTagCodeCsvList().hashCode();
            }
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static LoginProto.ProtoLoginInfo parseFrom(
                java.nio.ByteBuffer data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static LoginProto.ProtoLoginInfo parseFrom(
                java.nio.ByteBuffer data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static LoginProto.ProtoLoginInfo parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static LoginProto.ProtoLoginInfo parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static LoginProto.ProtoLoginInfo parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static LoginProto.ProtoLoginInfo parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static LoginProto.ProtoLoginInfo parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }

        public static LoginProto.ProtoLoginInfo parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static LoginProto.ProtoLoginInfo parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input);
        }

        public static LoginProto.ProtoLoginInfo parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static LoginProto.ProtoLoginInfo parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }

        public static LoginProto.ProtoLoginInfo parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        @java.lang.Override
        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(LoginProto.ProtoLoginInfo prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        @java.lang.Override
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE
                    ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }

        /**
         * Protobuf type {@code ProtoLoginInfo}
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:ProtoLoginInfo)
                LoginProto.ProtoLoginInfoOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return LoginProto.internal_static_ProtoLoginInfo_descriptor;
            }

            @java.lang.Override
            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return LoginProto.internal_static_ProtoLoginInfo_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                LoginProto.ProtoLoginInfo.class, LoginProto.ProtoLoginInfo.Builder.class);
            }

            // Construct using LoginProto.ProtoLoginInfo.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(
                    com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessageV3
                        .alwaysUseFieldBuilders) {
                }
            }

            @java.lang.Override
            public Builder clear() {
                super.clear();
                uname_ = "";

                pwd_ = "";

                acconeId_ = "";

                deviceModel_ = com.google.protobuf.LazyStringArrayList.EMPTY;
                bitField0_ = (bitField0_ & ~0x00000008);
                tagCodeCsv_ = java.util.Collections.emptyList();
                bitField0_ = (bitField0_ & ~0x00000010);
                return this;
            }

            @java.lang.Override
            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return LoginProto.internal_static_ProtoLoginInfo_descriptor;
            }

            @java.lang.Override
            public LoginProto.ProtoLoginInfo getDefaultInstanceForType() {
                return LoginProto.ProtoLoginInfo.getDefaultInstance();
            }

            @java.lang.Override
            public LoginProto.ProtoLoginInfo build() {
                LoginProto.ProtoLoginInfo result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            @java.lang.Override
            public LoginProto.ProtoLoginInfo buildPartial() {
                LoginProto.ProtoLoginInfo result = new LoginProto.ProtoLoginInfo(this);
                int from_bitField0_ = bitField0_;
                int to_bitField0_ = 0;
                result.uname_ = uname_;
                result.pwd_ = pwd_;
                result.acconeId_ = acconeId_;
                if (((bitField0_ & 0x00000008) == 0x00000008)) {
                    deviceModel_ = deviceModel_.getUnmodifiableView();
                    bitField0_ = (bitField0_ & ~0x00000008);
                }
                result.deviceModel_ = deviceModel_;
                if (((bitField0_ & 0x00000010) == 0x00000010)) {
                    tagCodeCsv_ = java.util.Collections.unmodifiableList(tagCodeCsv_);
                    bitField0_ = (bitField0_ & ~0x00000010);
                }
                result.tagCodeCsv_ = tagCodeCsv_;
                result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            @java.lang.Override
            public Builder clone() {
                return (Builder) super.clone();
            }

            @java.lang.Override
            public Builder setField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    java.lang.Object value) {
                return (Builder) super.setField(field, value);
            }

            @java.lang.Override
            public Builder clearField(
                    com.google.protobuf.Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }

            @java.lang.Override
            public Builder clearOneof(
                    com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }

            @java.lang.Override
            public Builder setRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    int index, java.lang.Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }

            @java.lang.Override
            public Builder addRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    java.lang.Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }

            @java.lang.Override
            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof LoginProto.ProtoLoginInfo) {
                    return mergeFrom((LoginProto.ProtoLoginInfo) other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(LoginProto.ProtoLoginInfo other) {
                if (other == LoginProto.ProtoLoginInfo.getDefaultInstance()) return this;
                if (!other.getUname().isEmpty()) {
                    uname_ = other.uname_;
                    onChanged();
                }
                if (!other.getPwd().isEmpty()) {
                    pwd_ = other.pwd_;
                    onChanged();
                }
                if (!other.getAcconeId().isEmpty()) {
                    acconeId_ = other.acconeId_;
                    onChanged();
                }
                if (!other.deviceModel_.isEmpty()) {
                    if (deviceModel_.isEmpty()) {
                        deviceModel_ = other.deviceModel_;
                        bitField0_ = (bitField0_ & ~0x00000008);
                    } else {
                        ensureDeviceModelIsMutable();
                        deviceModel_.addAll(other.deviceModel_);
                    }
                    onChanged();
                }
                if (!other.tagCodeCsv_.isEmpty()) {
                    if (tagCodeCsv_.isEmpty()) {
                        tagCodeCsv_ = other.tagCodeCsv_;
                        bitField0_ = (bitField0_ & ~0x00000010);
                    } else {
                        ensureTagCodeCsvIsMutable();
                        tagCodeCsv_.addAll(other.tagCodeCsv_);
                    }
                    onChanged();
                }
                this.mergeUnknownFields(other.unknownFields);
                onChanged();
                return this;
            }

            @java.lang.Override
            public final boolean isInitialized() {
                return true;
            }

            @java.lang.Override
            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                LoginProto.ProtoLoginInfo parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (LoginProto.ProtoLoginInfo) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            private int bitField0_;

            private java.lang.Object uname_ = "";

            /**
             * <code>string uname = 1;</code>
             */
            public java.lang.String getUname() {
                java.lang.Object ref = uname_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    uname_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }

            /**
             * <code>string uname = 1;</code>
             */
            public com.google.protobuf.ByteString
            getUnameBytes() {
                java.lang.Object ref = uname_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    uname_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }

            /**
             * <code>string uname = 1;</code>
             */
            public Builder setUname(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                uname_ = value;
                onChanged();
                return this;
            }

            /**
             * <code>string uname = 1;</code>
             */
            public Builder clearUname() {

                uname_ = getDefaultInstance().getUname();
                onChanged();
                return this;
            }

            /**
             * <code>string uname = 1;</code>
             */
            public Builder setUnameBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                uname_ = value;
                onChanged();
                return this;
            }

            private java.lang.Object pwd_ = "";

            /**
             * <code>string pwd = 2;</code>
             */
            public java.lang.String getPwd() {
                java.lang.Object ref = pwd_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    pwd_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }

            /**
             * <code>string pwd = 2;</code>
             */
            public com.google.protobuf.ByteString
            getPwdBytes() {
                java.lang.Object ref = pwd_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    pwd_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }

            /**
             * <code>string pwd = 2;</code>
             */
            public Builder setPwd(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                pwd_ = value;
                onChanged();
                return this;
            }

            /**
             * <code>string pwd = 2;</code>
             */
            public Builder clearPwd() {

                pwd_ = getDefaultInstance().getPwd();
                onChanged();
                return this;
            }

            /**
             * <code>string pwd = 2;</code>
             */
            public Builder setPwdBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                pwd_ = value;
                onChanged();
                return this;
            }

            private java.lang.Object acconeId_ = "";

            /**
             * <code>string acconeId = 3;</code>
             */
            public java.lang.String getAcconeId() {
                java.lang.Object ref = acconeId_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    acconeId_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }

            /**
             * <code>string acconeId = 3;</code>
             */
            public com.google.protobuf.ByteString
            getAcconeIdBytes() {
                java.lang.Object ref = acconeId_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    acconeId_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }

            /**
             * <code>string acconeId = 3;</code>
             */
            public Builder setAcconeId(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                acconeId_ = value;
                onChanged();
                return this;
            }

            /**
             * <code>string acconeId = 3;</code>
             */
            public Builder clearAcconeId() {

                acconeId_ = getDefaultInstance().getAcconeId();
                onChanged();
                return this;
            }

            /**
             * <code>string acconeId = 3;</code>
             */
            public Builder setAcconeIdBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                acconeId_ = value;
                onChanged();
                return this;
            }

            private com.google.protobuf.LazyStringList deviceModel_ = com.google.protobuf.LazyStringArrayList.EMPTY;

            private void ensureDeviceModelIsMutable() {
                if (!((bitField0_ & 0x00000008) == 0x00000008)) {
                    deviceModel_ = new com.google.protobuf.LazyStringArrayList(deviceModel_);
                    bitField0_ |= 0x00000008;
                }
            }

            /**
             * <code>repeated string deviceModel = 4;</code>
             */
            public com.google.protobuf.ProtocolStringList
            getDeviceModelList() {
                return deviceModel_.getUnmodifiableView();
            }

            /**
             * <code>repeated string deviceModel = 4;</code>
             */
            public int getDeviceModelCount() {
                return deviceModel_.size();
            }

            /**
             * <code>repeated string deviceModel = 4;</code>
             */
            public java.lang.String getDeviceModel(int index) {
                return deviceModel_.get(index);
            }

            /**
             * <code>repeated string deviceModel = 4;</code>
             */
            public com.google.protobuf.ByteString
            getDeviceModelBytes(int index) {
                return deviceModel_.getByteString(index);
            }

            /**
             * <code>repeated string deviceModel = 4;</code>
             */
            public Builder setDeviceModel(
                    int index, java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                ensureDeviceModelIsMutable();
                deviceModel_.set(index, value);
                onChanged();
                return this;
            }

            /**
             * <code>repeated string deviceModel = 4;</code>
             */
            public Builder addDeviceModel(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                ensureDeviceModelIsMutable();
                deviceModel_.add(value);
                onChanged();
                return this;
            }

            /**
             * <code>repeated string deviceModel = 4;</code>
             */
            public Builder addAllDeviceModel(
                    java.lang.Iterable<java.lang.String> values) {
                ensureDeviceModelIsMutable();
                com.google.protobuf.AbstractMessageLite.Builder.addAll(
                        values, deviceModel_);
                onChanged();
                return this;
            }

            /**
             * <code>repeated string deviceModel = 4;</code>
             */
            public Builder clearDeviceModel() {
                deviceModel_ = com.google.protobuf.LazyStringArrayList.EMPTY;
                bitField0_ = (bitField0_ & ~0x00000008);
                onChanged();
                return this;
            }

            /**
             * <code>repeated string deviceModel = 4;</code>
             */
            public Builder addDeviceModelBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);
                ensureDeviceModelIsMutable();
                deviceModel_.add(value);
                onChanged();
                return this;
            }

            private java.util.List<com.google.protobuf.ByteString> tagCodeCsv_ = java.util.Collections.emptyList();

            private void ensureTagCodeCsvIsMutable() {
                if (!((bitField0_ & 0x00000010) == 0x00000010)) {
                    tagCodeCsv_ = new java.util.ArrayList<com.google.protobuf.ByteString>(tagCodeCsv_);
                    bitField0_ |= 0x00000010;
                }
            }

            /**
             * <code>repeated bytes tagCodeCsv = 5;</code>
             */
            public java.util.List<com.google.protobuf.ByteString>
            getTagCodeCsvList() {
                return java.util.Collections.unmodifiableList(tagCodeCsv_);
            }

            /**
             * <code>repeated bytes tagCodeCsv = 5;</code>
             */
            public int getTagCodeCsvCount() {
                return tagCodeCsv_.size();
            }

            /**
             * <code>repeated bytes tagCodeCsv = 5;</code>
             */
            public com.google.protobuf.ByteString getTagCodeCsv(int index) {
                return tagCodeCsv_.get(index);
            }

            /**
             * <code>repeated bytes tagCodeCsv = 5;</code>
             */
            public Builder setTagCodeCsv(
                    int index, com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                ensureTagCodeCsvIsMutable();
                tagCodeCsv_.set(index, value);
                onChanged();
                return this;
            }

            /**
             * <code>repeated bytes tagCodeCsv = 5;</code>
             */
            public Builder addTagCodeCsv(com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                ensureTagCodeCsvIsMutable();
                tagCodeCsv_.add(value);
                onChanged();
                return this;
            }

            /**
             * <code>repeated bytes tagCodeCsv = 5;</code>
             */
            public Builder addAllTagCodeCsv(
                    java.lang.Iterable<? extends com.google.protobuf.ByteString> values) {
                ensureTagCodeCsvIsMutable();
                com.google.protobuf.AbstractMessageLite.Builder.addAll(
                        values, tagCodeCsv_);
                onChanged();
                return this;
            }

            /**
             * <code>repeated bytes tagCodeCsv = 5;</code>
             */
            public Builder clearTagCodeCsv() {
                tagCodeCsv_ = java.util.Collections.emptyList();
                bitField0_ = (bitField0_ & ~0x00000010);
                onChanged();
                return this;
            }

            @java.lang.Override
            public final Builder setUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return super.setUnknownFieldsProto3(unknownFields);
            }

            @java.lang.Override
            public final Builder mergeUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return super.mergeUnknownFields(unknownFields);
            }


            // @@protoc_insertion_point(builder_scope:ProtoLoginInfo)
        }

        // @@protoc_insertion_point(class_scope:ProtoLoginInfo)
        private static final LoginProto.ProtoLoginInfo DEFAULT_INSTANCE;

        static {
            DEFAULT_INSTANCE = new LoginProto.ProtoLoginInfo();
        }

        public static LoginProto.ProtoLoginInfo getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<ProtoLoginInfo>
                PARSER = new com.google.protobuf.AbstractParser<ProtoLoginInfo>() {
            @java.lang.Override
            public ProtoLoginInfo parsePartialFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return new ProtoLoginInfo(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<ProtoLoginInfo> parser() {
            return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<ProtoLoginInfo> getParserForType() {
            return PARSER;
        }

        @java.lang.Override
        public LoginProto.ProtoLoginInfo getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_ProtoLoginInfo_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_ProtoLoginInfo_fieldAccessorTable;

    public static com.google.protobuf.Descriptors.FileDescriptor
    getDescriptor() {
        return descriptor;
    }

    private static com.google.protobuf.Descriptors.FileDescriptor
            descriptor;

    static {
        java.lang.String[] descriptorData = {
                "\n\026protofiles/Login.proto\"g\n\016ProtoLoginIn" +
                        "fo\022\r\n\005uname\030\001 \001(\t\022\013\n\003pwd\030\002 \001(\t\022\020\n\010accone" +
                        "Id\030\003 \001(\t\022\023\n\013deviceModel\030\004 \003(\t\022\022\n\ntagCode" +
                        "Csv\030\005 \003(\014B\014B\nLoginProtob\006proto3"
        };
        com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
                new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
                    public com.google.protobuf.ExtensionRegistry assignDescriptors(
                            com.google.protobuf.Descriptors.FileDescriptor root) {
                        descriptor = root;
                        return null;
                    }
                };
        com.google.protobuf.Descriptors.FileDescriptor
                .internalBuildGeneratedFileFrom(descriptorData,
                        new com.google.protobuf.Descriptors.FileDescriptor[]{
                        }, assigner);
        internal_static_ProtoLoginInfo_descriptor =
                getDescriptor().getMessageTypes().get(0);
        internal_static_ProtoLoginInfo_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_ProtoLoginInfo_descriptor,
                new java.lang.String[]{"Uname", "Pwd", "AcconeId", "DeviceModel", "TagCodeCsv",});
    }

    // @@protoc_insertion_point(outer_class_scope)
}
